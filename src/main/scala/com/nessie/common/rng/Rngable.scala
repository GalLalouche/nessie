/*
Copyright (c) 2013 National ICT Australia Limited

All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:
1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.
3. The name of the author may not be used to endorse or promote products
   derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
// Adapted from https://github.com/NICTA/rng
package com.nessie.common.rng

import scala.math.Ordering.Implicits._
import scala.util.Random

import scalaz.{-\/, \/-, Free, Monad, OptionT, StreamT}
import scalaz.syntax.monad.ToMonadOps

import common.rich.RichT._
import common.rich.primitives.RichBoolean._
import common.Percentage
import common.rich.collections.RichSeq._

class Rngable[A](private val free: Free[Generator, A]) {
  def map[B](f: A => B): Rngable[B] = new Rngable(free map f)
  def flatMap[B](f: A => Rngable[B]): Rngable[B] = new Rngable(free flatMap (f(_).free))

  private def resume: RngResume[A] = free.resume match {
    case -\/(x) => RngCont(x.map(new Rngable(_)))
    case \/-(x) => RngTerm(x)
  }

  def random(stdGen: StdGen): (A, StdGen) = {
    @annotation.tailrec
    def aux(g: Rngable[A], r: StdGen): (A, StdGen) = g.resume match {
      case RngCont(Generator(f)) =>
        val (nextValue, nextGen) = f(r)
        aux(nextValue, nextGen)
      case RngTerm(a) => a -> r
    }

    aux(this, stdGen)
  }
  def mkRandom(stdGen: StdGen): A = random(stdGen)._1
}

object Rngable {
  def pure[A](a: A): Rngable[A] = Generator((a, _)).lift

  def fromRandom[A](f: Random => A): Rngable[A] = Generator {std =>
    val random = std.random
    f(random) -> StdGen(random.nextLong)
  }.lift
  def fromStdGen[A](f: StdGen => A): Rngable[A] = Generator {stdGen =>
    val (s1, s2) = stdGen.split
    f(s1) -> s2
  }.lift
  implicit val LongEv: Rngable[Long] = fromRandom(_.nextLong())
  implicit val IntEv: Rngable[Int] = LongEv.map(_.toInt)
  implicit val BooleanEv: Rngable[Boolean] = fromRandom(_.nextBoolean())
  def intRange(min: Int, max: Int): Rngable[Int] = {
    val range = max - min
    require(range > 0)
    fromRandom(_.nextInt(range) + min)
  }
  implicit val DoubleEv: Rngable[Double] = fromRandom(_.nextDouble())
  implicit val FloatEv: Rngable[Float] = DoubleEv.map(_.toInt)
  implicit val CharEv: Rngable[Char] = fromRandom(_.nextPrintableChar())
  def boolean(p: Percentage): Rngable[Boolean] = DoubleEv.map(p > _)
  def stringAtLength(length: Int): Rngable[String] = fromRandom(_.nextString(length))

  def sample[A](seq: IndexedSeq[A]): Rngable[A] = {
    require(seq.nonEmpty)
    intRange(0, seq.size).map(seq.apply)
  }
  def shuffle[A](seq: IndexedSeq[A]): Rngable[Seq[A]] = fromRandom(seq.shuffle)

  def keepWithProbability[A](p: Percentage, as: TraversableOnce[A]): Rngable[List[A]] = {
    def aux(as: List[A]): Rngable[List[A]] = as match {
      case Nil => Rngable.pure(Nil)
      case x :: xs => for {
        keepX <- boolean(p)
        tail <- aux(xs)
      } yield if (keepX) x :: tail else tail
    }
    aux(as.toList)
  }

  implicit object ScalazInstances extends Monad[Rngable] {
    override def point[A](a: => A): Rngable[A] = Rngable.pure(a)
    override def bind[A, B](fa: Rngable[A])(f: A => Rngable[B]): Rngable[B] = fa.flatMap(f)
  }

  type RngableIterable[A] = StreamT[Rngable, A]
  def singleton[A](value: Rngable[A]): Rngable.RngableIterable[A] =
    StreamT.fromStream(value.map(Stream(_)))

  object ToRngableOps {
    def mkRandom[A: Rngable]: Rngable[A] = implicitly[Rngable[A]]

    def mkRandom[A: Rngable](rng: StdGen): A = implicitly[Rngable[A]].mkRandom(rng)
    implicit class RngableTraversableOnce[A](xs: TraversableOnce[A]) {
      def sample: Rngable[A] = Rngable.sample(xs.toIndexedSeq)
      def shuffle: Rngable[Seq[A]] = Rngable.shuffle(xs.toIndexedSeq)
    }

    implicit class RichRngableIterable[A](private val $: RngableIterable[A]) extends AnyVal {
      def random(stdGen: StdGen): (Stream[A], StdGen) = {
        val (s1, s2) = stdGen.split
        $.uncons.map {
          case Some((head, tail)) => head #:: tail.mkRandom(s1)
          case None => Stream.empty
        }.random(s2)
      }
      // TODO handle duplication with definition of Rngable
      def mkRandom(stdGen: StdGen): Stream[A] = random(stdGen)._1
      def last: Rngable[A] = $.toStream.map(_.last)
    }
  }

  type RngableOption[A] = OptionT[Rngable, A]
  def none[A]: RngableOption[A] = OptionT.none
  def some[A](a: A): RngableOption[A] = OptionT.some[Rngable, A](a)
  // TODO move to MoreOptionTOps or something similar.
  def when[A](b: Boolean)(a: => Rngable[A]): RngableOption[A] = whenM(Rngable.pure(b))(a)
  def whenM[A](bm: Rngable[Boolean])(a: => Rngable[A]): RngableOption[A] = for {
    b <- bm.liftM[OptionT]
    result <- if (b) a.liftM[OptionT] else none
  } yield result
  def unless[A](b: Boolean)(a: => Rngable[A]): RngableOption[A] = when(b.isFalse)(a)
  def unlessM[A](bm: Rngable[Boolean])(a: => Rngable[A]): RngableOption[A] =
    whenM(bm.map(_.isFalse))(a)

  def iterate[A](a: A)(f: A => Rngable[A]): RngableIterable[A] =
    iterateOptionally(a)(f(_).map(Option(_)) |> OptionT.apply)
  // TODO unfoldMFromA
  private def notFirst[A](a: A) = (a, a -> false)
  def iterateOptionally[A](a: A)(f: A => RngableOption[A]): RngableIterable[A] =
    StreamT.unfoldM((a, true)) {
      case (a, isFirst) => (if (isFirst) some(notFirst(a)) else f(a).map(notFirst)).run
    }

  def tryNTimes[A](n: Int)(r: => RngableOption[A]): RngableOption[A] =
    if (n == 0) none else r.orElse(tryNTimes(n - 1)(r))
}

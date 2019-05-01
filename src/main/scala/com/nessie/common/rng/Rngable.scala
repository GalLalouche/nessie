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

import common.Percentage
import scalaz.{-\/, Free, \/-}

import scala.collection.mutable.ArrayBuffer
import scala.math.Ordering.Implicits._
import scala.util.Random

class Rngable[A](private val free: Free[Generator, A]) {
  def map[B](f: A => B): Rngable[B] = new Rngable(free map f)
  def flatMap[B](f: A => Rngable[B]): Rngable[B] = new Rngable(free flatMap (f(_).free))

  private def resume: RngResume[A] =
    free.resume match {
      case -\/(x) => RngCont(x.map(new Rngable(_)))
      case \/-(x) => RngTerm(x)
    }

  def random(stdGen: StdGen): (A, StdGen) = {
    @annotation.tailrec
    def aux(g: Rngable[A], r: StdGen): (A, StdGen) = g.resume match {
      case RngCont(Generator(f)) => aux(f(r), r.nextGen)
      case RngTerm(a) => a -> r
    }

    aux(this, stdGen)
  }
  def mkRandom(stdGen: StdGen): A = random(stdGen)._1
}

object Rngable {
  def pure[A](a: A): Rngable[A] = Generator(_ => a).lift

  private def fromRandom[A](f: Random => A): Rngable[A] = Generator(std => f(std.random)).lift
  implicit val LongEv: Rngable[Long] = fromRandom(_.nextLong())
  implicit val IntEv: Rngable[Int] = LongEv.map(_.toInt)
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
  //implicit def IterableEv[A](implicit ev: Rng[A]): Rng[Iterable[A]] = (rng: StdGen) => {
  //  val (rng1, rng2) = rng.split
  //  ev.mkRandoms(rng1) -> rng2
  //}

  def sample[A](seq: IndexedSeq[A]): Rngable[A] = {
    require(seq.nonEmpty)
    intRange(0, seq.size).map(seq.apply)
  }
  def shuffle[A](seq: IndexedSeq[A]): Rngable[IndexedSeq[A]] = {
    val array = ArrayBuffer[A]()
    array.sizeHint(seq.size)
    array ++= seq
    fromRandom(random => {
      for (n <- array.length - 1 to 0 by -1) {
        val k = random.nextInt(n + 1)
        val (a, b) = (array(n), array(k))
        array(k) = a
        array(n) = b
      }
      array.toVector
    })
  }
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

  trait ToRngableOps {
    def mkRandom[A: Rngable]: Rngable[A] = implicitly[Rngable[A]]
    def mkRandom[A: Rngable](rng: StdGen): A = implicitly[Rngable[A]].mkRandom(rng)
    implicit class RngableTraversableOnce[A](xs: TraversableOnce[A]) {
      def sample: Rngable[A] = Rngable.sample(xs.toIndexedSeq)
    }
  }

  def main(args: Array[String]): Unit = {
    def randomVector(n: Int): Rngable[Vector[Int]] =
      if (n <= 0) Rngable.pure[Vector[Int]](Vector.empty) else for {
        x <- IntEv
        y <- IntEv
        result <- randomVector(n - 2).map(Vector(x, y) ++ _)
      } yield result
    println(randomVector(100000).mkRandom(StdGen.fromSeed(0)))
  }
}

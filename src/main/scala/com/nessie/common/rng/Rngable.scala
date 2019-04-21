package com.nessie.common.rng

import com.nessie.common.RichIterable

import scala.util.Random

import scalaz.Monad
import scalaz.syntax.ToFunctorOps

trait Rngable[A] {
  def random(rng: StdGen): (A, StdGen)
  def mkRandom(rng: StdGen): A = random(rng)._1
  def randoms(rng: StdGen): Iterable[(A, StdGen)] =
    RichIterable.from(Iterator.iterate(random(rng))(e => random(e._2)))
  def mkRandoms(rng: StdGen): Iterable[A] = randoms(rng).map(_._1)

  def map[B](f: A => B): Rngable[B] = (rng: StdGen) => {
    val (a, next) = random(rng)
    f(a) -> next
  }
  def flatMap[B](f: A => Rngable[B]): Rngable[B] = (rng: StdGen) => {
    val (a, next) = random(rng)
    f(a).random(next)
  }
}

object Rngable extends ToFunctorOps {
  def pure[A](a: A): Rngable[A] = (rng: StdGen) => (a, rng)
  implicit object MonadEv extends Monad[Rngable] {
    override def bind[A, B](fa: Rngable[A])(f: A => Rngable[B]): Rngable[B] = fa.flatMap(f)
    override def map[A, B](fa: Rngable[A])(f: A => B): Rngable[B] = fa.map(f)
    override def point[A](a: => A): Rngable[A] = Rngable.pure(a)
  }

  private def fromRandom[A](f: Random => A): Rngable[A] = (rng: StdGen) => {
    val (_, next) = rng.next
    f(rng.random) -> next
  }
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
  def stringAtLength(length: Int): Rngable[String] = fromRandom(_.nextString(length))
  implicit def IterableEv[A](implicit ev: Rngable[A]): Rngable[Iterable[A]] = (rng: StdGen) => {
    val (rng1, rng2) = rng.split
    ev.mkRandoms(rng1) -> rng2
  }

  trait ToRngableOps {
    def mkRandom[A: Rngable]: Rngable[A] = implicitly[Rngable[A]]
    def mkRandom[A: Rngable](rng: StdGen): A = implicitly[Rngable[A]].mkRandom(rng)
    def mkRandoms[A: Rngable]: Rngable[Iterable[A]] = implicitly[Rngable[Iterable[A]]]
  }
}

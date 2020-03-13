package com.nessie.common

import scala.collection.immutable.IndexedSeq

import scalaz.{Foldable, MonadPlus, Monoid}

// TODO move to ScalaCommon
object EnumUtils {
  implicit object IndexedSeqEv extends MonadPlus[IndexedSeq] with Foldable[IndexedSeq] {
    override def bind[A, B](fa: IndexedSeq[A])(f: A => IndexedSeq[B]) = fa flatMap f
    override def point[A](a: => A) = IndexedSeq(a)
    override def empty[A] = IndexedSeq.empty
    override def plus[A](a: IndexedSeq[A], b: => IndexedSeq[A]) = a ++ b

    // Optimized implementation
    override def foldRight[A, B](fa: IndexedSeq[A], z: => B)(f: (A, => B) => B): B =
      fa.foldRight(z)(f(_, _))
    override def foldMap[A, B: Monoid](fa: IndexedSeq[A])(f: A => B): B =
      fa.map(f).fold(Monoid[B].zero)(Monoid[B].append(_, _))
    override def foldLeft[A, B](fa: IndexedSeq[A], z: B)(f: (B, A) => B) = fa.foldLeft(z)(f)
  }
}

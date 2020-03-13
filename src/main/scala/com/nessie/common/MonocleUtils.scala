package com.nessie.common

import scala.language.higherKinds

import scalaz.Apply
import scalaz.syntax.apply._
import monocle.{Iso, Lens, PLens}
import monocle.std.function._

object MonocleUtils {
  // Unsafe
  def castingIso[A, B <: A]: Iso[A, B] = Iso[A, B](_.asInstanceOf[B])(identity)
  def unsafeLens[A, B](f: A => Option[B])(g: B => A => Option[A]): Lens[A, B] = {
    Lens[A, B](f(_).get)(a => b => g(a)(b).get)
  }
  def unsafeMapLens[A, B](a: A): Lens[Map[A, B], B] = Lens[Map[A, B], B](_ (a))(b => _ + (a -> b))
  def unsafeCovariance[A, B, C <: A](l: Lens[C, B]): Lens[A, B] =
    Lens[A, B](l get _.asInstanceOf[C])(a => s => l.set(a)(s.asInstanceOf[C]))

  // Safe
  // TODO move to common
  def optionalLens[A, B](f: A => Option[B])(g: B => A => Option[A]): PLens[A, Option[A], Option[B], B] =
    PLens[A, Option[A], Option[B], B](f)(g)
  def lift[A, B, C[_] : Apply](lens: Lens[A, B]): Lens[C[A], C[B]] = {
    Lens[C[A], C[B]](implicitly[Apply[C]].lift(lens.get))(b => a => ^(a, b)(flipped(lens.set)(_)(_)))
  }
}

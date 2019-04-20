package com.nessie.common

import monocle.std.FunctionOptics
import monocle.{Iso, Lens, PLens}

import scala.language.higherKinds
import scalaz.Apply
import scalaz.syntax.ToApplicativeOps

object MonocleUtils extends ToApplicativeOps with FunctionOptics {
  // Unsafe
  def castingIso[A, B <: A]: Iso[A, B] = Iso[A, B](_.asInstanceOf[B])(identity)
  def unsafeLens[A, B](f: A => Option[B])(g: B => A => Option[A]): Lens[A, B] = {
    Lens[A, B](f(_).get)(a => b => g(a)(b).get)
  }
  def unsafeCovariance[A, B, C <: A](l: Lens[C, B]): Lens[A, B] =
    Lens[A, B](l get _.asInstanceOf[C])(a => s => l.set(a)(s.asInstanceOf[C]))

  // Safe
  // TODO move to commmon
  def optionalLens[A, B](f: A => Option[B])(g: B => A => Option[A]): PLens[A, Option[A], Option[B], B] =
    PLens[A, Option[A], Option[B], B](f)(g)
  def lift[A, B, C[_] : Apply](lens: Lens[A, B]): Lens[C[A], C[B]] = {
    Lens[C[A], C[B]](implicitly[Apply[C]].lift(lens.get))(b => a => ^(a, b)(flipped(lens.set)(_)(_)))
  }
}

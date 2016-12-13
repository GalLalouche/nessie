package com.nessie.common

import monocle.std.FunctionOptics
import monocle.{Iso, Lens, PLens}

import scalaz.Apply
import scalaz.syntax.ToApplicativeOps

object MonocleUtils extends ToApplicativeOps with FunctionOptics {
  // unsafe
  def castingIso[A, B <: A] = Iso[A, B](_.asInstanceOf[B])(identity)
  def optionalLens[A, B](f: A => Option[B])(g: B => A => Option[A]) =
    PLens[A, Option[A], Option[B], B](f)(g)
  def unsafeLens[A, B](f: A => Option[B])(g: B => A => Option[A]): Lens[A, B] = {
    Lens[A, B](f(_).get)(a => b => g(a)(b).get)
  }
  def lift[A, B, C[_] : Apply](lens: Lens[A, B]): Lens[C[A], C[B]] = {
    Lens[C[A], C[B]](implicitly[Apply[C]].lift(lens.get))(b => a =>
      ^(a, b)(flipped(lens.set)(_)(_)))
  }
}

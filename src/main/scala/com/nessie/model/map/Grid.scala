package com.nessie.model.map

import common.rich.func.MoreIterableInstances

import scalaz.Functor
import scalaz.syntax.ToFunctorOps

trait Grid[A] extends ToFunctorOps with MoreIterableInstances {
  def width: Int
  def height: Int
  def place(p: MapPoint, o: A): Grid[A]
  def apply(p: MapPoint): A

  require(width > 0)
  require(height > 0)

  def map[B](f: A => B): Grid[B]
  val size = GridSize(width, height)
  def isInBounds(mp: MapPoint): Boolean = size.isInBounds(mp)
}

object Grid {
  implicit object FunctorEv extends Functor[Grid] {
    override def map[A, B](fa: Grid[A])(f: A => B): Grid[B] = fa.map(f)
  }
}

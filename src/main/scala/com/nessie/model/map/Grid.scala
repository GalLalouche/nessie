package com.nessie.model.map

import common.rich.func.MoreIterableInstances

import scalaz.syntax.ToFunctorOps

trait Grid[A] extends ToFunctorOps with MoreIterableInstances {
  def width: Int
  def height: Int
  def place(p: MapPoint, o: A): Grid[A]
  def apply(p: MapPoint): A

  require(width > 0)
  require(height > 0)
}

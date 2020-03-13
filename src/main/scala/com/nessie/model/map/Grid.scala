package com.nessie.model.map

import scalaz.Functor

trait Grid[A] {
  def width: Int
  def height: Int
  def points: Iterable[MapPoint] = for {y <- 0 until height; x <- 0 until width} yield MapPoint(x = x, y = y)
  def place(p: MapPoint, o: A): Grid[A]
  def apply(p: MapPoint): A

  require(width > 0)
  require(height > 0)

  def map[B](f: A => B): Grid[B]
  def mapPoints[B](f: (MapPoint, A) => B): Grid[B]
  val size = GridSize(width, height)
  def isInBounds(mp: MapPoint): Boolean = size.isInBounds(mp)
}

object Grid {
  implicit object FunctorEv extends Functor[Grid] {
    override def map[A, B](fa: Grid[A])(f: A => B): Grid[B] = fa.map(f)
  }
}

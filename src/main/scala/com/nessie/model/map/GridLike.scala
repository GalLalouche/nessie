package com.nessie.model.map

import common.rich.func.MoreIterableInstances
import common.rich.primitives.RichBoolean._
import common.rich.RichT._
import monocle.Lens
import scalax.collection.GraphEdge.UnDiEdge
import scalax.collection.GraphPredef._
import scalax.collection.immutable.Graph
import scalaz.syntax.ToFunctorOps

import scala.language.higherKinds

trait GridLike[G, A]
    extends ToFunctorOps with MoreIterableInstances {self: G =>
  protected def gridLens: Lens[G, Grid[A]]
  def width: Int = gridLens.get(this).width
  def height: Int = gridLens.get(this).height

  def size: GridSize = GridSize(width, height)
  // TODO rename MapPoint to LatticePoint or do *something* about the naming dissonance
  @inline def isInBounds(p: MapPoint): Boolean = p.x >= 0 && p.y >= 0 && p.x < width && p.y < height
  @inline private def checkBounds(p: MapPoint): Unit =
    if (isInBounds(p).isFalse)
      throw new IndexOutOfBoundsException(
        s"Point <$p> is out of bounds for map of dimensions <($width, $height)>")
  @inline def isBorder(p: MapPoint): Boolean = p.x == 0 || p.y == 0 || p.x == width - 1 || p.y == height - 1

  @inline private def internalPlace(p: MapPoint, o: A): G = gridLens.modify(_.place(p, o))(this)
  def place(p: MapPoint, o: A): G = {
    checkBounds(p)
    internalPlace(p, o)
  }

  @inline private def internalApply(p: MapPoint): A = gridLens.get(this)(p)
  def apply(p: MapPoint): A = {
    checkBounds(p)
    internalApply(p)
  }

  def points: Iterable[MapPoint] = for (y <- 0 until height; x <- 0 until width) yield MapPoint(x, y)
  def objects: Iterable[(MapPoint, A)] = points fproduct apply

  def neighbors(mp: MapPoint): Iterable[MapPoint] = mp.neighbors.filter(isInBounds)

  def fill(c: A): G = map(c.const)
  def map(f: A => A): G = mapPoints((_, a) => f(a))
  def mapPoints(f: (MapPoint, A) => A): G = gridLens.modify(_.mapPoints(f))(this)

  lazy val toFullGraph: Graph[MapPoint, UnDiEdge] = Graph.from(
    nodes = points,
    edges = for {
      x <- 0 until width
      y <- 0 until height
      point = MapPoint(x, y)
      neighbor <- Vector(point.go(Direction.Down), point.go(Direction.Right))
      if isInBounds(neighbor)
    } yield point ~ neighbor,
  )
}

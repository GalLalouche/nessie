package com.nessie.model.map

import com.nessie.common.graph.RichUndirected._
import common.rich.RichT._
import common.rich.RichTuple._
import common.rich.func.{MoreIterableInstances, MoreSetInstances}
import common.rich.primitives.RichBoolean._
import scalax.collection.Graph
import scalax.collection.GraphEdge.UnDiEdge
import scalax.collection.GraphPredef._

import scalaz.syntax.ToFunctorOps

/** An map of a given level without between-objects, so walls and its ilks taken up a full tile. */
// TODO replace template method with composition.
abstract class BattleMap(val width: Int, val height: Int)
    extends ToFunctorOps with MoreSetInstances with MoreIterableInstances {
  require(width > 0)
  require(height > 0)

  @inline def isInBounds(p: MapPoint): Boolean = p.x >= 0 && p.y >= 0 && p.x < width && p.y < height
  @inline private def checkBounds(p: MapPoint): Unit =
    if (isInBounds(p).isFalse)
      throw new IndexOutOfBoundsException(
        s"Point <$p> is out of bounds for map of dimensions <($width, $height)>")
  @inline def isBorder(p: MapPoint): Boolean = p.x == 0 || p.y == 0 || p.x == width - 1 || p.y == height - 1

  protected def internalPlace(p: MapPoint, o: BattleMapObject): BattleMap
  def place(p: MapPoint, o: BattleMapObject): BattleMap = {
    checkBounds(p)
    internalPlace(p, o)
  }

  protected def internalApply(p: MapPoint): BattleMapObject
  def apply(p: MapPoint): BattleMapObject = {
    checkBounds(p)
    internalApply(p)
  }

  def points: Iterable[MapPoint] = for (y <- 0 until height; x <- 0 until width) yield MapPoint(x, y)
  def objects: Iterable[(MapPoint, BattleMapObject)] = points fproduct apply

  // Using eq for singletons is potentially faster.
  def isEmptyAt(p: MapPoint): Boolean = EmptyMapObject eq apply(p)
  def isOccupiedAt(p: MapPoint): Boolean = isEmptyAt(p).isFalse
  def remove(p: MapPoint): BattleMap = place(p, EmptyMapObject)

  def neighbors(mp: MapPoint): Iterable[MapPoint] = mp.neighbors.filter(isInBounds)
  private def reachableNeighbors(mp: MapPoint): Iterable[MapPoint] =
    if (this (mp).canMoveThrough) neighbors(mp).filter((apply _).andThen(_.canMoveThrough)) else Nil

  lazy val toObjectGraph: Graph[(MapPoint, BattleMapObject), UnDiEdge] = {
    val nodes: Iterable[(MapPoint, BattleMapObject)] = objects
    val edges: Iterable[UnDiEdge[(MapPoint, BattleMapObject)]] = points
        .flatMap(o => reachableNeighbors(o).map(o -> _))
        .map(_.map(_ :-> apply).reduce(UnDiEdge.apply))
    Graph.from(nodes, edges)
  }

  lazy val toPointGraph: Graph[MapPoint, UnDiEdge] = toObjectGraph.mapNodes(_._1)
  lazy val passablePointGraph: Graph[MapPoint, UnDiEdge] =
    toObjectGraph.filterNodes(_._2.canMoveThrough).mapNodes(_._1)

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

  def foldPoints: ((BattleMap, MapPoint) => BattleMap) => BattleMap = points.foldLeft(this)
}

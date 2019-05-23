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

  protected def internalPlace(p: MapPoint, o: BattleMapObject): BattleMap

  def size: Int = width * height
  def replace(p: MapPoint, o: BattleMapObject): BattleMap = remove(p).place(p, o)
  def replaceSafely(p: MapPoint, o: BattleMapObject): BattleMap =
    removeSafely(p).mapIf(o neq EmptyMapObject).to(_.place(p, o))

  def points: Iterable[MapPoint] = for (y <- 0 until height; x <- 0 until width) yield MapPoint(x, y)
  def objects: Iterable[(MapPoint, BattleMapObject)] = points fproduct apply

  def isInBounds(p: MapPoint): Boolean = p.x >= 0 && p.y >= 0 && p.x < width && p.y < height
  def isBorder(p: MapPoint): Boolean = p.x == 0 || p.y == 0 || p.x == width - 1 || p.y == height - 1

  def apply(p: MapPoint): BattleMapObject

  private def checkBounds(p: MapPoint): Unit =
    if (p.x >= width || p.y >= height)
      throw new IndexOutOfBoundsException(
        s"Point <$p> is out of bounds for map of dimensions <($width, $height)>")
  def place(p: MapPoint, o: BattleMapObject): BattleMap = {
    require(o != EmptyMapObject, "Don't place empty objects. Use remove instead.")
    checkBounds(p)
    if (isOccupiedAt(p)) throw new MapOccupiedException(p) else internalPlace(p, o)
  }

  // Using eq for singletons is potentially faster.
  def isEmptyAt(p: MapPoint): Boolean = EmptyMapObject eq apply(p)
  def isOccupiedAt(p: MapPoint): Boolean = isEmptyAt(p).isFalse

  def remove(p: MapPoint): BattleMap =
    if (isOccupiedAt(p)) this.internalPlace(p, EmptyMapObject)
    else throw new MapEmptyException(p)
  def removeSafely(p: MapPoint): BattleMap =
    this.mapIf(isOccupiedAt(p)).to(_.internalPlace(p, EmptyMapObject))

  /**
   * Moves an object from one location to another
   *
   * @param src The location of the object
   * @throws MapEmptyException If the map empty at src
   */
  def move(src: MapPoint) = {
    val o = this (src)
    new {
      /**
       * Moves an object to the location
       *
       * @param dst The location to move to
       * @return The modified controller
       * @throws MapOccupiedException If there's already an object at dst
       */
      def to(dst: MapPoint): BattleMap = remove(src).place(dst, o)
    }
  }

  lazy val toObjectGraph: Graph[(MapPoint, BattleMapObject), UnDiEdge] = {
    val nodes: Iterable[(MapPoint, BattleMapObject)] = objects
    val edges: Iterable[UnDiEdge[(MapPoint, BattleMapObject)]] = points
        .flatMap(o => reachableNeighbors(o).map(o -> _))
        .map(_.map(_ :-> apply).reduce(UnDiEdge.apply))
    Graph.from(nodes, edges)
  }

  lazy val toPointGraph: Graph[MapPoint, UnDiEdge] = toObjectGraph.mapNodes(_._1)

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
  def clearAllPoints: BattleMap = foldPoints(_ removeSafely _)
  /** Marks the points as a FullWall and places walls around it. */
  def fillItAll: BattleMap = foldPoints(_.replaceSafely(_, FullWall))

  def neighbors(mp: MapPoint): Iterable[MapPoint] = mp.neighbors.filter(isInBounds)
  def neighborsAndDiagonals(mp: MapPoint): Iterable[MapPoint] = mp.neighborsAndDiagonals.filter(isInBounds)
  def reachableNeighbors(mp: MapPoint): Iterable[MapPoint] =
    if (this (mp).canMoveThrough) neighbors(mp).filter((apply _).andThen(_.canMoveThrough)) else Nil
}

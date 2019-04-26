package com.nessie.model.map

import com.nessie.common.graph.RichUndirected._
import common.rich.RichT._
import common.rich.RichTuple._
import common.rich.func.{MoreIterableInstances, MoreSetInstances}
import common.rich.primitives.RichBoolean._
import scalax.collection.GraphEdge.UnDiEdge
import scalax.collection.immutable.Graph

import scalaz.syntax.ToFunctorOps

/** An (immutable) map of a given level without between-objects, so walls and its ilks taken up a full tile. */
// TODO replace template method with composition.
abstract class SimpleBattleMap(val width: Int, val height: Int)
    extends ToFunctorOps with MoreSetInstances with MoreIterableInstances {
  require(width > 0)
  require(height > 0)

  protected def internalPlace(p: MapPoint, o: BattleMapObject): SimpleBattleMap

  def size: Int = width * height
  def replace(p: MapPoint, o: BattleMapObject): SimpleBattleMap = remove(p).place(p, o)
  def replaceSafely(p: MapPoint, o: BattleMapObject): SimpleBattleMap = removeSafely(p).place(p, o)

  def points: Iterable[MapPoint] = for (y <- 0 until height; x <- 0 until width) yield MapPoint(x, y)
  def objects: Iterable[(MapPoint, BattleMapObject)] = points fproduct apply

  def isInBounds(p: MapPoint): Boolean = p.x >= 0 && p.y >= 0 && p.x < width && p.y < height

  def apply(p: MapPoint): BattleMapObject
  private def isPassable(mp: MapPoint): Boolean = SimpleBattleMap.isPassable(this (mp))

  private def checkBounds(p: MapPoint): Unit =
    if (p.x >= width || p.y >= height)
      throw new IndexOutOfBoundsException(
        s"Point <$p> is out of bounds for map of dimensions <($width, $height)>")
  def place(p: MapPoint, o: BattleMapObject): SimpleBattleMap = {
    require(o != EmptyMapObject, "Don't place empty objects. Use remove instead.")
    checkBounds(p)
    if (isOccupiedAt(p)) throw new MapOccupiedException(p) else internalPlace(p, o)
  }

  // Using eq for singletons is potentially faster.
  def isEmptyAt(p: MapPoint): Boolean = EmptyMapObject eq apply(p)
  def isOccupiedAt(p: MapPoint): Boolean = isEmptyAt(p).isFalse

  def remove(p: MapPoint): SimpleBattleMap =
    if (isOccupiedAt(p)) this.internalPlace(p, EmptyMapObject)
    else throw new MapEmptyException(p)
  def removeSafely(p: MapPoint): SimpleBattleMap =
    this.mapIf(isOccupiedAt(p)).to(_.internalPlace(p, EmptyMapObject))

  /**
   * Moves an object from one location to another
   * @param src The location of the object
   * @throws MapEmptyException If the map empty at src
   */
  def move(src: MapPoint) = {
    val o = this (src)
    new {
      /**
       * Moves an object to the location
       * @param dst The location to move to
       * @return The modified controller
       * @throws MapOccupiedException If there's already an object at dst
       */
      def to(dst: MapPoint): SimpleBattleMap = remove(src).place(dst, o)
    }
  }

  lazy val toObjectGraph: Graph[(MapPoint, BattleMapObject), UnDiEdge] = {
    val nodes: Iterable[(MapPoint, BattleMapObject)] = objects
    val edges: Iterable[UnDiEdge[(MapPoint, BattleMapObject)]] = points
        .flatMap(o => reachableNeighbors(o).map(o -> _))
        .map(_.map(_ |-> apply).reduce(UnDiEdge.apply))
    Graph.from(nodes, edges)
  }

  lazy val toPointGraph: Graph[MapPoint, UnDiEdge] =
  // TODO add mapEdges to RichGraph
    Graph.from(points, toObjectGraph.properEdges.map {case UnDiEdge((p1, _), (p2, _)) => UnDiEdge(p1, p2)})

  def foldPoints: ((SimpleBattleMap, MapPoint) => SimpleBattleMap) => SimpleBattleMap = points.foldLeft(this)
  def clearAllPoints: SimpleBattleMap = foldPoints(_ removeSafely _)
  /** Marks the points as a FullWall and places walls around it. */
  def fillItAll: SimpleBattleMap = foldPoints(_.place(_, FullWall))

  def neighbors(mp: MapPoint): Iterable[MapPoint] = mp.neighbors.filter(isInBounds)
  def reachableNeighbors(mp: MapPoint): Iterable[MapPoint] =
    if (isPassable(mp)) neighbors(mp).filter((apply _).andThen(SimpleBattleMap.isPassable)) else Nil
}

object SimpleBattleMap {
  // TODO RichT.neq
  // TODO replace with a method on BattleMapObject
  // TODO add overloads implemented using simple ints, for performance?
  private def isPassable(o: BattleMapObject): Boolean = o.eq(FullWall).isFalse
}

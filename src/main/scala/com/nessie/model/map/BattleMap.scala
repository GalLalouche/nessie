package com.nessie.model.map

import common.rich.RichT._
import common.rich.func.{MoreIterableInstances, MoreSetInstances}
import common.rich.primitives.RichBoolean._
import monocle.Lens
import scalax.collection.GraphEdge.UnDiEdge
import scalax.collection.immutable.Graph

import scala.util.Try

import scalaz.syntax.ToFunctorOps

/** An (immutable) map of a given level. */
abstract class BattleMap(val width: Int, val height: Int)
    extends ToFunctorOps with MoreSetInstances with MoreIterableInstances {
  require(width > 0)
  require(height > 0)

  protected def internalPlace(pd: DirectionalMapPoint, o: BetweenMapObject): BattleMap
  protected def internalPlace(p: MapPoint, o: BattleMapObject): BattleMap

  def size: Int = width * height
  def replace(p: MapPoint, o: BattleMapObject): BattleMap = remove(p).place(p, o)
  def replaceSafely(p: MapPoint, o: BattleMapObject): BattleMap = removeSafely(p).place(p, o)

  def replace(dmp: DirectionalMapPoint, o: BetweenMapObject): BattleMap = remove(dmp).place(dmp, o)
  def replaceSafely(dmp: DirectionalMapPoint, o: BetweenMapObject): BattleMap = removeSafely(dmp).place(dmp, o)

  def points: Iterable[MapPoint] = for (y <- 0 until height; x <- 0 until width) yield MapPoint(x, y)
  def objects: Iterable[(MapPoint, BattleMapObject)] = points fproduct apply

  def isInBounds(p: MapPoint): Boolean = p.x >= 0 && p.y >= 0 && p.x < width && p.y < height

  def apply(p: MapPoint): BattleMapObject
  def apply(pd: DirectionalMapPoint): BetweenMapObject

  private def checkBounds(p: MapPoint): Unit =
    if (p.x >= width || p.y >= height)
      throw new IndexOutOfBoundsException(
        s"Point <$p> is out of bounds for map of dimensions <($width, $height)>")
  def place(p: MapPoint, o: BattleMapObject): BattleMap = {
    require(o != EmptyMapObject, "Don't place empty objects. Use remove instead.")
    checkBounds(p)
    if (isOccupiedAt(p)) throw new MapOccupiedException(p) else internalPlace(p, o)
  }
  def place(pd: DirectionalMapPoint, o: BetweenMapObject): BattleMap = {
    checkBounds(pd.toPoint)
    require(o != EmptyBetweenMapObject, "Don't place empty objects. Use remove instead.")
    if (isOccupiedAt(pd)) throw new MapOccupiedException(pd) else internalPlace(pd, o)
  }

  // Using eq for singletons is potentially faster
  def isEmptyAt(p: MapPoint): Boolean = EmptyMapObject eq apply(p)
  def isEmptyAt(pd: DirectionalMapPoint): Boolean = EmptyBetweenMapObject eq apply(pd)
  def isOccupiedAt(p: MapPoint): Boolean = isEmptyAt(p).isFalse
  def isOccupiedAt(pd: DirectionalMapPoint): Boolean = isEmptyAt(pd).isFalse

  def remove(p: MapPoint): BattleMap =
    if (isOccupiedAt(p)) this.internalPlace(p, EmptyMapObject)
    else throw new MapEmptyException(p)
  def removeSafely(p: MapPoint): BattleMap =
    this.mapIf(isOccupiedAt(p)).to(_.internalPlace(p, EmptyMapObject))
  def remove(pd: DirectionalMapPoint): BattleMap =
    if (isOccupiedAt(pd)) this.internalPlace(pd, EmptyBetweenMapObject)
    else throw new MapEmptyException(pd)
  def removeSafely(pd: DirectionalMapPoint): BattleMap =
    this.mapIf(isOccupiedAt(pd)).to(internalPlace(pd, EmptyBetweenMapObject))

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
      def to(dst: MapPoint): BattleMap = remove(src).place(dst, o)
    }
  }

  def betweenPoints: Iterable[DirectionalMapPoint] = {
    // Take the bottom and right border of every cell, in addition to the left side of the first column and
    // the top side of the first row.
    val topBorder = points.view.filter(_.y == 0).map(DirectionalMapPoint(_, Direction.Up))
    val leftBorder = points.view.filter(_.x == 0).map(DirectionalMapPoint(_, Direction.Left))
    val rest = points.view.flatMap(p => Vector(
      DirectionalMapPoint(p, Direction.Down),
      DirectionalMapPoint(p, Direction.Right),
    ))
    (topBorder ++ leftBorder ++ rest).toVector
  }
  def betweenObjects: Iterable[(DirectionalMapPoint, BetweenMapObject)] = betweenPoints fproduct apply

  lazy val toPointGraph: Graph[MapPoint, UnDiEdge] = {
    val vertices = points.toSet
    val edges = betweenObjects.filter {
      case (_, Wall) => false
      case (_, EmptyBetweenMapObject) => true
    }.map(_._1).flatMap {pd =>
      Try(pd.toPoint.go(pd.direction))
          .filter(vertices)
          .map(UnDiEdge(pd.toPoint, _))
          .toOption
    }
    Graph.from(vertices, edges)
  }

  def foldPoints: ((BattleMap, MapPoint) => BattleMap) => BattleMap = points.foldLeft(this)
  def clearAllPoints: BattleMap = foldPoints(_ removeSafely _)
  /** Marks the points as a FullWall and places walls around it. */
  def fillItAll: BattleMap = foldPoints(_.place(_, FullWall))
  def foldBetweenPoints: ((BattleMap, DirectionalMapPoint) => BattleMap) => BattleMap =
    betweenPoints.foldLeft(this)
  /** Adds walls between all points. */
  def wallItUp: BattleMap = foldBetweenPoints(_.place(_, Wall))
  /** Marks all points as a FullWall. */
  def fill(next: MapPoint): BattleMap =
    DirectionalMapPoint.around(next).foldLeft(place(next, FullWall))(_.replaceSafely(_, Wall))

  def isBorder(dmp: DirectionalMapPoint): Boolean =
    (dmp.x == 0 && dmp.direction == Direction.Left) ||
        (dmp.y == 0 && dmp.direction == Direction.Up) ||
        (dmp.x == width - 1 && dmp.direction == Direction.Right) ||
        (dmp.y == height - 1 && dmp.direction == Direction.Down)
  def neighbors(mp: MapPoint): Iterable[MapPoint] = mp.neighbors.filter(isInBounds)
  /** Neighbors without a wall in between. */
  def reachableNeighbors(mp: MapPoint): Iterable[MapPoint] = mp.neighbors.view
      .filter(isInBounds)
      .filter(other => isEmptyAt(DirectionalMapPoint.between(mp, other)))
      .filter(apply(_).eq(FullWall).isFalse) // TODO add a method to object to check if can traverse to
      .toStream
}

object BattleMap {
  def pointLens(p: MapPoint): Lens[BattleMap, BattleMapObject] =
    Lens[BattleMap, BattleMapObject](_.apply(p))(o => m => m.internalPlace(p, o))
}

package com.nessie.model.map

import com.nessie.common.rng.StdGen
import common.rich.RichT._
import common.rich.func.MoreSetInstances
import monocle.Lens
import scalax.collection.GraphEdge.UnDiEdge
import scalax.collection.immutable.Graph

import scala.util.Try

import scalaz.syntax.ToFunctorOps

/**
 * A map of a given level.
 * Is immutable.
 * @param width The map's width
 * @param height The map's height
 */
abstract class BattleMap(val width: Int, val height: Int)
    extends ToFunctorOps with MoreSetInstances {
  require(width > 0)
  require(height > 0)

  def size = width * height
  def replace(unitLocation: MapPoint, o: BattleMapObject): BattleMap =
    remove(unitLocation).place(unitLocation, o)

  def points: Iterable[(MapPoint, BattleMapObject)] =
    for (y <- 0 until height; x <- 0 until width) yield
      MapPoint(x, y).mapTo(p => p -> apply(p))

  def isInBounds(p: MapPoint): Boolean =
    p.x >= 0 && p.y >= 0 && p.x < width && p.y < height

  def apply(p: MapPoint): BattleMapObject
  def apply(pd: DirectionalMapPoint): BetweenMapObject

  private def checkBounds(p: MapPoint) {
    if (p.x >= width || p.y >= height)
      throw new IndexOutOfBoundsException(
        s"Point <$p> is out of bounds for map of dimensions <($width, $height)>")
  }
  def place(p: MapPoint, o: BattleMapObject): BattleMap = {
    require(o != EmptyMapObject, "Don't place empty objects. Use remove instead.")
    checkBounds(p)
    if (isOccupiedAt(p))
      throw new MapOccupiedException(p)
    else
      internalPlace(p, o)
  }
  def place(pd: DirectionalMapPoint, o: BetweenMapObject): BattleMap = {
    checkBounds(pd.toPoint)
    require(o != EmptyBetweenMapObject, "Don't place empty objects. Use remove instead.")
    if (isOccupiedAt(pd))
      throw new MapOccupiedException(pd)
    else
      internalPlace(pd, o)
  }

  def isOccupiedAt(p: MapPoint): Boolean = !isEmptyAt(p)
  def isOccupiedAt(pd: DirectionalMapPoint): Boolean = !isEmptyAt(pd)
  def isEmptyAt(p: MapPoint): Boolean = apply(p) == EmptyMapObject
  def isEmptyAt(pd: DirectionalMapPoint): Boolean = apply(pd) == EmptyBetweenMapObject

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
      def to(dst: MapPoint): BattleMap = {
        remove(src).place(dst, o)
      }
    }
  }

  def betweens: Iterable[(DirectionalMapPoint, BetweenMapObject)] = points.map(_._1)
      .flatMap(p => Direction.values.map(DirectionalMapPoint(p, _))).toSet.fproduct(apply)

  protected def internalPlace(pd: DirectionalMapPoint, o: BetweenMapObject): BattleMap
  protected def internalPlace(p: MapPoint, o: BattleMapObject): BattleMap

  lazy val toGraph: Graph[MapPoint, UnDiEdge] = {
    val vertices = points.map(_._1).toSet
    val edges = betweens.filter {
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

  /** Adds walls between all points. */
  def wallItUp: BattleMap = betweens.map(_._1).foldLeft(this)(_.place(_, Wall))
  /** Marks all points as a FullWall. */
  def fillItAll: BattleMap = points.map(_._1).foldLeft(this)(_.place(_, FullWall))
  /** Marks the points as a FullWall and places walls around it. */
  def fill(next: MapPoint): BattleMap = {
    // TODO extract to method
    Direction.values.map(DirectionalMapPoint(next, _)).foldLeft(place(next, FullWall))(
      // TODO replace safely
      (map, dmp) => map.mapIf(_.isEmptyAt(dmp)).to(map.place(dmp, Wall)))

  }


  def isBorder(dmp: DirectionalMapPoint): Boolean =
    (dmp.x == 0 && dmp.direction == Direction.Left) ||
        (dmp.y == 0 && dmp.direction == Direction.Up) ||
        (dmp.x == width - 1 && dmp.direction == Direction.Right) ||
        (dmp.y == height - 1 && dmp.direction == Direction.Down)
  def neighbors(mp: MapPoint): Set[MapPoint] = mp.neighbors.filter(isInBounds).toSet
  /** Neighbors without a wall in between. */
  def reachableNeighbors(mp: MapPoint): Set[MapPoint] = mp.neighbors.view
      .filter(isInBounds)
      .filter(other => isEmptyAt(DirectionalMapPoint.between(mp, other)))
      .filter(apply(_) != FullWall) // TODO add a method to object to check if can traverse to
      .toSet
}

object BattleMap {
  def pointLens(p: MapPoint) =
    Lens[BattleMap, BattleMapObject](_.apply(p))(o => m => m.internalPlace(p, o))
}

package com.nessie.map.model

import com.nessie.map.{BattleMapObject, CombatUnitObject, EmptyMapObject}
import monocle.Lens

/**
 * A map of a given level.
 * Is immutable.
 * @param width  The map's width
 * @param height The map's height
 */
abstract class BattleMap(val width: Int, val height: Int) {

  require(width > 0)
  require(height > 0)

  def modify(p: MapPoint, f: BattleMapObject => BattleMapObject): BattleMap =
    if (isOccupiedAt(p))
      forcePlace(p, f(this (p)))
    else
      throw new MapEmptyException(p)

  def replace(unitLocation: MapPoint, o: BattleMapObject): BattleMap =
    remove(unitLocation).place(unitLocation, o)

  def points: Iterable[(MapPoint, BattleMapObject)] = {
    for (
      y <- 0 until height;
      x <- 0 until width
    ) yield {
      val p = MapPoint(x, y)
      p -> this (p)
    }
  }

  def nonEmpty: Iterable[(MapPoint, BattleMapObject)] = points.filterNot(_._2 == EmptyMapObject)

  /**
   * @param p The point to look at
   * @return The object at point p
   */
  def apply(p: MapPoint): BattleMapObject

  /**
   * @param p The point to place at
   * @param o The object to place
   * @return A modified map, with o at p
   */
  protected def forcePlace(p: MapPoint, o: BattleMapObject): BattleMap

  /**
   * Places an object on the map
   * @param p The point to place at
   * @param o The object to place
   * @return A new controller, with the modified map
   * @throws MapOccupiedException if the map already has an object at p.
   *                              use { @link BattleMapModifier#remove(MapPoint)} first.
   */
  def place(p: MapPoint, o: BattleMapObject): BattleMap =
    if (isOccupiedAt(p)) throw new MapOccupiedException(p) else forcePlace(p, o)

  /**
   * @param p The point to check at
   * @return true iff the map is occupied at p
   */
  def isOccupiedAt(p: MapPoint): Boolean = this(p) != EmptyMapObject

  /**
   * Removes an object from the map
   * @param p The point to remove from
   * @return The modified controller, with the modified map
   * @throws MapEmptyException if there is no object at p
   */
  def remove(p: MapPoint): BattleMap =
    if (isOccupiedAt(p))
      this.forcePlace(p, EmptyMapObject)
    else
      throw new MapEmptyException(p)

  /**
   * Moves an object from one location to another
   * @param src The location of the object
   * @throws MapEmptyException If the map empty at src
   */
  def move(src: MapPoint) = {
    val o = this(src)
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
}

object BattleMap {
  def pointLens(p: MapPoint) = Lens[BattleMap, BattleMapObject](_.apply(p))(o => m => m.forcePlace(p, o))
}

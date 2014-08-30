package com.nessie.map.model

import com.nessie.model.map.objects.BattleMapObject
import com.nessie.model.map.objects.EmptyMapObject
import com.nessie.map.exceptions.{MapOccupiedException, MapEmptyException}

/**
 * Implements some basic modifier of a map, and checks.
 * Is immutable
 * @param map The map to wrap
 */
class BattleMapModifier private(val map: BattleMap) {
	require(map != null)

	/**
	 * @param p The point to look at
	 * @return The object at point p
	 */
	def apply(p: MapPoint) = map(p)

	/**
	 * Places an object on the map
	 * @param p The point to place at
	 * @param o The object to place
	 * @return A new controller, with the modified map
	 * @throws MapOccupiedException if the map already has an object at p.
	 *                              use { @link BattleMapModifier#remove(MapPoint)} first.
	 */
	def place(p: MapPoint, o: BattleMapObject): BattleMapModifier =
		if (isOccupiedAt(p)) throw new MapOccupiedException(p) else BattleMapModifier(map.place(p, o))

	/**
	 * @param p The point to check at
	 * @return true iff the map is occupied at p
	 */
	def isOccupiedAt(p: MapPoint): Boolean = map(p) != EmptyMapObject

	/**
	 * Removes an object from the map
	 * @param p The point to remove from
	 * @return The modified controller, with the modified map
	 * @throws MapEmptyException if there is no object at p
	 */
	def remove(p: MapPoint): BattleMapModifier =
		if (isOccupiedAt(p) == false) throw new MapEmptyException(p)
		else BattleMapModifier(map.place(p, EmptyMapObject))

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
				remove(src).place(dst, o).map
			}
		}
	}
}

object BattleMapModifier {
	/**
	 * Creates a new BattleMapModifier
	 * @param m The map to wrap
	 * @return A controller
	 */
	implicit def apply(m: BattleMap): BattleMapModifier = new BattleMapModifier(m)
}

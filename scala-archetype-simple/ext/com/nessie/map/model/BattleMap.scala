package com.nessie.map.model

import com.nessie.model.map.objects.BattleMapObject

/**
 * A map of a given level.
 * Is immutable.
 * @param width The map's width
 * @param height The map's height
 */
abstract class BattleMap(val width: Int, val height: Int) extends Traversable[(MapPoint, BattleMapObject)] {
	require(width > 0)
	require(height > 0)

	override def foreach[T](f: ((MapPoint, BattleMapObject)) => T) = {
		for (
			y <- 0 until height;
			x <- 0 until width
		) {
			f((x, y), this(x, y))
		}
	}

	/**
	 * @param p The point to look at
	 * @return The object at point p
	 */
	def apply(p: MapPoint): BattleMapObject

	def apply(x: Int, y: Int): BattleMapObject = apply((x, y))

	/**
	 * @param p The point to place at
	 * @param o The object to place
	 * @return A modified map, with o at p
	 */
	def place(p: MapPoint, o: BattleMapObject): BattleMap

	def place(x: Int, y: Int, o: BattleMapObject): BattleMap = place((x, y), o)
}
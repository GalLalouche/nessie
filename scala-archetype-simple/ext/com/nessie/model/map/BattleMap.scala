package com.nessie.model.map

import com.nessie.model.map.objects.BattleMapObject

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

	def apply(p: MapPoint): BattleMapObject

	def apply(x: Int, y: Int): BattleMapObject = apply((x, y))

	def place(p: MapPoint, o: BattleMapObject): BattleMap

	def place(x: Int, y: Int, o: BattleMapObject): BattleMap = place((x, y), o)
}
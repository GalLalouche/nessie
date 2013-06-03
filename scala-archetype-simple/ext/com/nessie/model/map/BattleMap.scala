package com.nessie.model.map

import com.nessie.model.map.objects.BattleMapObject

object BattleMap {
	implicit def tupleToMapPoint(p: (Int, Int)) = new MapPoint(p._1, p._2)
}

abstract class BattleMap(val width: Int, val height: Int) extends Traversable[(MapPoint, BattleMapObject)] {
	import BattleMap.tupleToMapPoint
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
	def update(p: MapPoint, o: BattleMapObject): Unit
	def update(x: Int, y: Int, o: BattleMapObject): Unit = update((x, y), o)
}
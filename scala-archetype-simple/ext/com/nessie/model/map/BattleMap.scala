package com.nessie.model.map

abstract class BattleMap(val width: Int, val height: Int) extends Traversable[(Int, Int, BattleMapObject)] {
	require(width > 0)
	require(height > 0)
	
	override def foreach[T](f: ((Int, Int, BattleMapObject)) => T) = {
		for (
			x <- 0 until width;
			y <- 0 until height
		) {
			f(x, y, this(x, y))
		}
	}

	def apply(p: MapPoint): BattleMapObject
	def update(p: MapPoint, o: BattleMapObject): Unit
}
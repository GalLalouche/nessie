package com.nessie.model.map

abstract class BattleMap(val width: Int, val height: Int) extends Traversable[(Int, Int, BattleMapObject)] {
	require(width > 0)
	require(height > 0)

	def apply(p: MapPoint): BattleMapObject
	def update(p: MapPoint, o: BattleMapObject): Unit
}
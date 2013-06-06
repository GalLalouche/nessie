package com.nessie.map.model

import com.nessie.model.map.objects.BattleMapObject
import com.nessie.model.map.objects.EmptyMapObject

/**
 * Implements a map as a List[List] matrix
 * @param list The matrix list
 */
class ArrayBattleMap private(list: List[List[BattleMapObject]]) extends BattleMap(list.length, list(0).length) {
	override def apply(p: MapPoint) = list(p.x)(p.y)

	override def place(p: MapPoint, o: BattleMapObject) = new ArrayBattleMap(list.updated(p.x, list(p.x).updated(p.y, o)))
}

object ArrayBattleMap {
	/**
	 * @param width The map's width
	 * @param height The map's height
	 * @return A new immutable map, whose cell are all empty to begin with
	 */
	def apply(width: Int, height: Int) = new ArrayBattleMap({
		require(width > 0)
		require(height > 0)
		List.fill(width)(List.fill[BattleMapObject](height)(EmptyMapObject))
	})
}
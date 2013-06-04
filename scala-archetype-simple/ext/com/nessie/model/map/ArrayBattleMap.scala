package com.nessie.model.map

import com.nessie.model.map.objects.BattleMapObject
import com.nessie.model.map.objects.EmptyMapObject


class ArrayBattleMap(list: List[List[BattleMapObject]]) extends BattleMap(list.length, list(0).length) {
	override def apply(p: MapPoint) = list(p.x)(p.y)

	override def place(p: MapPoint, o: BattleMapObject) = new ArrayBattleMap(list.updated(p.x, list(p.x).updated(p.y, o)))
}

object ArrayBattleMap {
	def apply(width: Int, height: Int) = new ArrayBattleMap({
		require(width > 0)
		require(height > 0)
		List.fill(width)(List.fill[BattleMapObject](height)(EmptyMapObject))
	})
}

trait T

object O extends T

class B {
	val x: List[T] = (1 until 10) map (x => O) toList
}

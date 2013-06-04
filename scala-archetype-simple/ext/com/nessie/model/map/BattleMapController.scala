package com.nessie.model.map

import com.nessie.model.map.objects.BattleMapObject
import com.nessie.model.map.objects.EmptyMapObject


class BattleMapController(val map: BattleMap) {
	require(map != null)

	def apply(p: MapPoint) = if (isOccupied(p)) map(p) else throw new MapEmptyException(p)

	def place(p: MapPoint, o: BattleMapObject) =
		if (isOccupied(p)) throw new MapOccupiedException(p) else new BattleMapController(map.place(p, o))

	def isOccupied(p: MapPoint): Boolean = map(p) != EmptyMapObject

	def remove(p: MapPoint): BattleMapController = if (isOccupied(p) == false) throw new MapEmptyException(p)
	else new BattleMapController(map.place(p, EmptyMapObject))

	def move(src: MapPoint) = {
		val o = this(src)
		new {
			def to(dst: MapPoint) = {
				remove(src).place(dst, o)
			}
		}
	}
}
package com.nessie.model.map


class BattleMapController(map: BattleMap) {
	require(map != null)
	
	final def apply(p: MapPoint) = if (isOccupied(p)) map(p) else throw new MapEmptyException(p)
	final def update(p: MapPoint, o: BattleMapObject) = if (isOccupied(p)) throw new MapOccupiedException(p) else map(p) = o
	final def isOccupied(p: MapPoint): Boolean = map(p) != EmptyMapObject
	final def remove(p: MapPoint): BattleMapObject = {
		val $ = this(p)
		map(p) = EmptyMapObject
		$
	}
	final def move(src: MapPoint) = {
		val o = this(src)
		new {
			def to(dst: MapPoint) = {
				update(dst, o)
				remove(src)
			}
		}
	}
}
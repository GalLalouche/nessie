package com.nessie.model.map

import com.nessie.model.map.objects.BattleMapObject
import com.nessie.model.map.objects.EmptyMapObject



class ArrayBattleMap(width: Int, height: Int) extends BattleMap(width, height) {
	val array = Array.ofDim[BattleMapObject](width, height)
	for (cell <- this) this(cell._1) = EmptyMapObject
	

	override def apply(p: MapPoint) = array(p._1)(p._2)
	override def update(p: MapPoint, o: BattleMapObject) = array(p._1)(p._2) = o

}
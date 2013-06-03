package com.nessie.model.map

class ArrayBattleMap(width: Int, height: Int) extends BattleMap(width, height) {
	val array = Array.ofDim[BattleMapObject](width, height)
	for (cell <- this.map(x => (x._1, x._2))) this(cell) = EmptyMapObject
	

	override def apply(p: MapPoint) = array(p._1)(p._2)
	override def update(p: MapPoint, o: BattleMapObject) = array(p._1)(p._2) = o

}
package com.nessie.model.map

class ArrayBattleMap(width: Int, height: Int) extends BattleMap(width, height) {
	val array = Array.ofDim[BattleMapObject](width, height)
	for (cell <- this.map(x => (x._1, x._2))) set(cell, EmptyMapObject)
	override def foreach[T](f: ((Int, Int, BattleMapObject)) => T) = {
		for (
			x <- 0 until array.length;
			y <- 0 until array(0).length
		) {
			f(x, y, array(x)(y))
		}
	}

	override def get(p: MapPoint) = array(p._1)(p._2)
	override def set(p: MapPoint, o: BattleMapObject) = array(p._1)(p._2) = o

}
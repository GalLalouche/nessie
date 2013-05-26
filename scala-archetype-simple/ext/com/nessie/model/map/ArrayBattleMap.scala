package com.nessie.model.map

class ArrayBattleMap(width: Int, height: Int) extends BattleMap(width, height) {
	val array = Array.ofDim[BattleMapObject](width, height)
	for (cell <- this.map(x => (x._1, x._2))) this(cell._1, cell._2) = EmptyMapObject
	override def foreach[T](f: ((Int, Int, BattleMapObject)) => T) = {
		for (
			x <- 0 until array.length;
			y <- 0 until array(0).length
		) {
			f(x, y, array(x)(y))
		}
	}

	override def apply(x: Int, y: Int) = array(x)(y)
	override def update(x:Int, y:Int, o:BattleMapObject) = array(x)(y) = o
	
}
package com.nessie.model.map

abstract class BattleMap(val width: Int, val height: Int) extends Traversable[(Int, Int, BattleMapObject)] {
	require(width > 0)
	require(height > 0)

	private def checkCoordinates(x: Int, y: Int) = {
		BattleMap.check(x, width)
		BattleMap.check(y, height)
	}

	def apply(p: MapPoint): BattleMapObject = apply(p._1, p._2)
	final def apply(x: Int, y: Int): BattleMapObject = apply(BattleMap.tupleToPoint(x, y))
	def update(p: MapPoint, o: BattleMapObject): Unit = update(p._1, p._2, o)
	final def update(x: Int, y: Int, o: BattleMapObject): Unit = update(BattleMap.tupleToPoint(x, y), o)
	final def isOccupied(x: Int, y: Int): Boolean = isOccupied(BattleMap.tupleToPoint(x, y))
	final def isOccupied(p: MapPoint): Boolean = apply(p) != EmptyMapObject
	final def remove(x: Int, y: Int): BattleMapObject = remove(BattleMap.tupleToPoint(x, y))
	final def remove(p: MapPoint): BattleMapObject =
		if (isOccupied(p)) {
			val $ = this(p)
			this(p) = EmptyMapObject
			$
		} else
			throw new IllegalStateException("map is empty at " + p)
	final def move(p: MapPoint) = {
		if (isOccupied(p) == false) throw new IllegalStateException
		val o = apply(p)
		new {
			def to(p: MapPoint) = update(p, o)
		}
	}

}

object BattleMap {
	private def check(input: Int, max: Int) = input match {
		case x if x < 0 => throw new IndexOutOfBoundsException(x + " was negative")
		case x if x >= max => throw new IndexOutOfBoundsException(x + " was larger than " + max)
		case _ => Unit
	}

	private def tupleToPoint(x: Int, y: Int): MapPoint = (x, y)
}
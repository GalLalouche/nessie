package com.nessie.model.map

abstract class BattleMap(val width: Int, val height: Int) extends Traversable[(Int, Int, BattleMapObject)] {
	require(width > 0)
	require(height > 0)

	private def checkCoordinates(x: Int, y: Int) = {
		BattleMap.check(x, width)
		BattleMap.check(y, height)
	}

	protected def get(p: MapPoint): BattleMapObject
	protected def set(p: MapPoint, o: BattleMapObject): Unit
	final def apply(x: Int, y: Int): BattleMapObject = apply(BattleMap.tupleToPoint(x, y))
	final def apply(p: MapPoint): BattleMapObject = if (isOccupied(p)) get(p) else throw new MapEmptyException(p)
	final def update(x: Int, y: Int, o: BattleMapObject): Unit = update(BattleMap.tupleToPoint(x, y), o)
	final def update(p: MapPoint, o: BattleMapObject): Unit = if (isOccupied(p) == false) set(p, o) else throw new MapOccupiedException(p)
	final def isOccupied(x: Int, y: Int): Boolean = isOccupied(BattleMap.tupleToPoint(x, y))
	final def isOccupied(p: MapPoint): Boolean = get(p) != EmptyMapObject
	final def remove(x: Int, y: Int): BattleMapObject = remove(BattleMap.tupleToPoint(x, y))
	final def remove(p: MapPoint): BattleMapObject = {
		val $ = this(p)
		set(p, EmptyMapObject)
		$
	}
	final def move(p: MapPoint) = {
		val o = this(p)
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
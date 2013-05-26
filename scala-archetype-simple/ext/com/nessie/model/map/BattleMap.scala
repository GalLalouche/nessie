package com.nessie.model.map

abstract class BattleMap(val width: Int, val height: Int) extends Traversable[(Int, Int, BattleMapObject)] {
	require(width > 0)
	require(height > 0)

	private def checkCoordinates(x: Int, y: Int) = {
		BattleMap.check(x, width)
		BattleMap.check(y, height)
	}

	def apply(x: Int, y: Int): BattleMapObject
	final def apply(p: MapPoint): BattleMapObject = apply(p._1, p._2)
	def update(x: Int, y: Int, o: BattleMapObject)
	final def update(p: MapPoint, o: BattleMapObject): Unit = update(p._1, p._2, o)
	final def isOccupied(x: Int, y: Int) = apply(x, y) != EmptyMapObject
}

object BattleMap {
	private def check(input: Int, max: Int) = input match {
		case x if x < 0 => throw new IndexOutOfBoundsException(x + " was negative")
		case x if x >= max => throw new IndexOutOfBoundsException(x + " was larger than " + max)
		case _ => Unit
	}

	private def tupleToPoint(x: Int, y: Int): MapPoint = (x, y)
}
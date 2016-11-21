package com.nessie.map.model

import com.nessie.map.exceptions.{MapEmptyException, MapOccupiedException}
import com.nessie.model.map.objects.{EmptyMapObject, BattleMapObject}

/**
	* A map of a given level.
	* @param width The map's width
	* @param height The map's height
	*/
abstract class BattleMap(val width: Int, val height: Int) extends Traversable[(MapPoint, BattleMapObject)] {
	require(width > 0)
	require(height > 0)

	override def foreach[T](f: ((MapPoint, BattleMapObject)) => T) = {
		for (y <- 0 until height;
				 x <- 0 until width) {
			val p = MapPoint(x, y)
			f(p -> apply(p))
		}
	}

	/**
		* @param p The point to look at
		* @return The object at point p
		*/
	def apply(p: MapPoint): BattleMapObject

	/**
		* @param p The point to place at
		* @param o The object to place
		* @return A modified map, with o at p
		*/
	protected def _place(p: MapPoint, o: BattleMapObject): BattleMap
	private def shouldBeEmpty(p: MapPoint): BattleMap =
		if (isOccupiedAt(p))
			throw new MapOccupiedException(p)
		else
			this
	private def shouldBeOccupied(p: MapPoint): BattleMap =
		if (!isOccupiedAt(p))
			throw new MapEmptyException(p)
		else
			this

	def place(p: MapPoint, o: BattleMapObject): BattleMap = shouldBeEmpty(p)._place(p, o)

	def column(c: Int): Seq[BattleMapObject] = (0 until height) map (MapPoint(c, _)) map apply
	def row(r: Int): Seq[BattleMapObject] = (0 until width) map (MapPoint(_, r)) map apply

	lazy val rows = (0 until height) map (row(_))
	lazy val columns = (0 until width) map (column(_))

	def isOccupiedAt(p: MapPoint) = apply(p) != EmptyMapObject
	def remove(p: MapPoint) = shouldBeOccupied(p)._place(p, EmptyMapObject)
	def move(from: MapPoint) = new {
		def to(to: MapPoint) = remove(from).place(to, apply(from))
	}
}

package com.nessie.map.model

class MapPoint(val x: Int, val y: Int) {
	def this(e: (Int, Int)) = this(e._1, e._2)
	require(x >= 0)
	require(y >= 0)

	override def toString() = (x, y).toString

	override def equals(p: Any) = {
		if (p.isInstanceOf[MapPoint] == false)
			p == (x, y)
		else {
			val other = p.asInstanceOf[MapPoint];
			other.x == x && other.y == y
		}
	}

	override def hashCode(): Int = x.hashCode * 17 + y.hashCode
}

object MapPoint {
	def apply(x: Int, y: Int) = new MapPoint(x, y)
	def apply(e: (Int, Int)) = new MapPoint(e)
}

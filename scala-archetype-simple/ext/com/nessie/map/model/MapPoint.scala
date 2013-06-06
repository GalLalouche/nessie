package com.nessie.map.model

class MapPoint(val x: Int, val y: Int) {
	require(x >= 0)
	require(y >= 0)

	override def toString() = (x, y).toString

	override def equals(p: Any) = {
		if (p.isInstanceOf[MapPoint] == false) false
		val other = p.asInstanceOf[MapPoint];
		other.x == x && other.y == y
	}

	override def hashCode(): Int = x.hashCode * 17 + y.hashCode
}

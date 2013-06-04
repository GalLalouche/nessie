package com.nessie.model

package object map {

	class MapPoint(val x: Int, val y: Int) {
		override def toString() = (x, y).toString

		override def equals(p: Any) = {
			if (p.isInstanceOf[MapPoint] == false) false
			val other = p.asInstanceOf[MapPoint];
			other.x == x && other.y == y
		}

		override def hashCode(): Int = x.hashCode * 17 + y.hashCode
	}

	implicit def tupleToMapPoint(p: (Int, Int)) = new MapPoint(p._1, p._2)
}
package com.nessie.map.model

case class MapPoint(x: Int, y: Int)

object MapPoint {
	implicit def tupleToPoint(tuple: (Int, Int)): MapPoint = MapPoint(tuple._1, tuple._2)
}

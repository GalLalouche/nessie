package com.nessie.map.model

case class MapPoint(x: Int, y: Int) {
  def manhattanDistanceTo(unitLocation: MapPoint): Int = Math.abs(x - unitLocation.x) + Math.abs(y - unitLocation.y)

  def this(e: (Int, Int)) = this(e._1, e._2)
	require(x >= 0)
	require(y >= 0)
}

object MapPoint {
	def apply(e: (Int, Int)) = new MapPoint(e)
}

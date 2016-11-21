package com.nessie.map.view

import com.nessie.map.model.MapPoint

case class CellClicked(p: MapPoint) {
	def this(x: Int, y: Int) = this(MapPoint(x, y))
}

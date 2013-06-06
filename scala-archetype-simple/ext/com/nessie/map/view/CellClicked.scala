package com.nessie.map.view

import com.nessie.map.model.MapPoint
import scala.swing.event.Event

case class CellClicked(val p: MapPoint) extends Event {
	def this(x: Int, y: Int) = this((x, y))

	override def equals(obj: Any) = obj.isInstanceOf[CellClicked] && obj.asInstanceOf[CellClicked].p == p

	override def hashCode(): Int = p.hashCode
}

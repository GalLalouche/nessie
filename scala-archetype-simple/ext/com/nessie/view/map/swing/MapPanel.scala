package com.nessie.view.map.swing

import scala.swing.GridPanel
import com.nessie.model.map.BattleMap
import com.nessie.model.map.MapPoint
import scala.swing.event.{ActionEvent, Event}

case class CellClicked(p: MapPoint) extends Event {
	override def equals(obj: Any) = obj.isInstanceOf[CellClicked] && obj.asInstanceOf[CellClicked].p == p

	override def hashCode(): Int = p.hashCode
}

class MapPanel(map: BattleMap, builder: SwingBuilder) extends GridPanel(map.width, map.height) {
	require(map != null)
	contents ++= map map (x => {
		val b = builder(x._2)
		listenTo(b)
		reactions += {
			case y: ActionEvent if (y.source == b) => publish(CellClicked(x._1))
		}
		b
	})

	override def toString = "MapPanel"
}
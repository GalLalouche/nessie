package com.nessie.view.map.swing

import scala.swing.GridPanel
import scala.swing.event.{ActionEvent, Event}
import com.nessie.map.model.{BattleMap, MapPoint}

case class CellClicked(val p: MapPoint) extends Event {
	override def equals(obj: Any) = obj.isInstanceOf[CellClicked] && obj.asInstanceOf[CellClicked].p == p

	override def hashCode(): Int = p.hashCode
}

class MapPanel(map: BattleMap, builder: SwingBuilder) extends GridPanel(map.width, map.height) {
	require(map != null)
	require(builder != null)
	contents ++= map map (x => {
		val b = builder(x._2)
		listenTo(b)
		reactions += {
			case y: ActionEvent if (y.source == b) => publish(CellClicked(x._1))
		}
		b
	})
	val BACKGROUND_COLOR = contents(0).background

	override def toString = "MapPanel"

	private implicit def pointToIndex(p: MapPoint) = p.y * map.width + p.x;

	def select(p: MapPoint) {
		require(p != null)
		contents(p).background = java.awt.Color.RED;
	}

	def unselect = contents.foreach(_.background = BACKGROUND_COLOR)

	def unselect(p: MapPoint) {
		if (p != null)
			contents(p).background = BACKGROUND_COLOR;
	}
}
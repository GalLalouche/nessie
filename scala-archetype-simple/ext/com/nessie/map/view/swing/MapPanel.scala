package com.nessie.map.view.swing

import scala.swing.GridPanel
import scala.swing.event.ActionEvent
import com.nessie.map.model.{ BattleMap, MapPoint }
import com.nessie.map.view.{ MapView, CellClicked, SwingBuilder }
import akka.actor.Actor
import scala.swing.Component
import com.nessie.map.view.SwingBuilder
import java.awt.Color
import javax.swing.plaf.ColorUIResource

class MapPanel(builder: SwingBuilder) extends MapView {

	override def toString = "MapPanel"
	var v: GridPanel = null
	val BACKGROUND_COLOR: Color = new ColorUIResource(238, 238, 238)
	override def generateMap(m: BattleMap): GridPanel = {
		v = new GridPanel(m.width, m.height) {
			contents ++= m map ({
				case (point, o) => {
					val b = builder(o)
					listenTo(b)
					reactions += {
						case y: ActionEvent if (y.source == b) => MapPanel.this.self ! CellClicked(point)
					}
					b
				}
			})
		}
		unselect
		v
	}
	private implicit def pointToIndex(p: MapPoint) = p.y * m.width + p.x;
	def select(p: MapPoint) {
		require(p != null)
		v.contents(p).background = java.awt.Color.RED;
	}
	def unselect = v.contents.foreach(_.background = BACKGROUND_COLOR)
	//
	//	def unselect(p: MapPoint) {
	//		if (p != null)
	//			contents(p).background = BACKGROUND_COLOR;
	//	}
}
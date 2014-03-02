package com.nessie.map.view.swing

import scala.swing.GridPanel
import scala.swing.event.ActionEvent
import com.nessie.map.model.{ BattleMap, MapPoint }
import com.nessie.map.view.{ MapActor, CellClicked, SwingBuilder }
import scala.swing.Component
import com.nessie.map.view.SwingBuilder
import java.awt.Color
import javax.swing.plaf.ColorUIResource
import com.nessie.map.view.MapView
import akka.actor.ActorRef

class MapPanel(builder: SwingBuilder, owner: ActorRef) extends MapView {
	require(builder != null)
	require(owner != null)
	override def toString = "MapPanel"
	private var v: GridPanel = null
	private var width: Int = -1
	private val BACKGROUND_COLOR: Color = new ColorUIResource(238, 238, 238)
	protected def createGridPanel(m: BattleMap): GridPanel = new GridPanel(m.width, m.height)
	override def generateMap(m: BattleMap): GridPanel = {
		v = createGridPanel(m)
		v.contents ++= m map ({
			case (point, o) => {
				val b = builder(o)
				v.listenTo(b)
				v.reactions += {
					case y: ActionEvent if (y.source == b) => owner ! CellClicked(point)
				}
				b
			}
		})
		unselect
		width = m.width
		v
	}
	private implicit def pointToIndex(p: MapPoint) = p.y * width + p.x;
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
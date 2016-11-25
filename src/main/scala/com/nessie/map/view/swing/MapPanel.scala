package com.nessie.map.view.swing

import java.awt.Color
import scala.swing.Component
import scala.swing.GridPanel
import scala.swing.event.ActionEvent
import com.nessie.map.model.BattleMap
import com.nessie.map.model.MapPoint
import com.nessie.map.view.MapView
import com.nessie.map.view.SwingBuilder
import com.nessie.map.view.SwingBuilder
import akka.actor.ActorRef
import javax.swing.plaf.ColorUIResource
import com.nessie.map.view.CellClicked

class MapPanel(builder: SwingBuilder, owner: ActorRef) extends MapView {
	require(builder != null)
	require(owner != null)
	override def toString = "MapPanel of " + v
	private var v: GridPanel = _
	private var width: Int = -1
	private val BACKGROUND_COLOR: Color = new ColorUIResource(238, 238, 238)
	protected def createGridPanel(m: BattleMap): GridPanel = new GridPanel(m.width, m.height)
	override def generateMap(m: BattleMap): GridPanel = {
		v = createGridPanel(m)
		v.contents ++= m.points map {
			case (point, o) =>
				val b = builder(o)
				v.listenTo(b)
				v.reactions += {
					case y: ActionEvent if y.source == b => owner ! CellClicked(point)
				}
				b
		}
		unselect
		width = m.width
		v
	}
	private def pointToIndex(p: MapPoint) = p.y * width + p.x
	def select(p: MapPoint) {
		require(p != null)
		v.contents(pointToIndex(p)).background = java.awt.Color.RED
	}
	def unselect = v.contents.foreach(_.background = BACKGROUND_COLOR)
}

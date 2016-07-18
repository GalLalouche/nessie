package com.nessie.map.view

import scala.swing.{ Component, Publisher }
import com.nessie.map.model.{ MapPoint, BattleMap }
import akka.actor.Actor
import com.nessie.map.model.MapPoint
import com.nessie.map.ctrl.SwingBattleMapController
import com.nessie.map.model.BattleMapModifier._
import com.nessie.map.view.swing.MapPanel
import scala.swing.SimpleSwingApplication
import com.nessie.map.view.swing.SimpleSwingBuilder

/**
 * This actor represents a mediator between the controller and the view.
 * It remembers some stuff about the view (e.g., which cell was last clicked) and notifies the view of any changes.
 * If any changes need to be made to the model, it notifies the controller.
 */
class MapActor extends Actor {
	import MapActor._
	val v = new MapPanel(new SimpleSwingBuilder(), self)
	def receive: Receive = {
		case GenerateMap(m: BattleMap) =>
			sender ! SwingBattleMapController.Map(v generateMap m)
			context become waitForFirstClick(m)
	}

	private def waitForFirstClick(m: BattleMap): Receive = {
		case CellClicked(p) if (m isOccupiedAt p) =>
			v select p
			context become waitForSecondClick(m, p)
	}

	private def waitForSecondClick(m: BattleMap, currentlySelect: MapPoint): Receive = {
		case CellClicked(p) => if (m isOccupiedAt p) () else {
			v.unselect
			context become receive
			println(context.parent)
			context.parent ! SwingBattleMapController.Move(currentlySelect, p)
		}
	}

}

object MapActor {
	case class GenerateMap(m: BattleMap)
}

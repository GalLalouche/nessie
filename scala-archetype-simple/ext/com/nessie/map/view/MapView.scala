package com.nessie.map.view

import scala.swing.{ Component, Publisher }
import com.nessie.map.model.{ MapPoint, BattleMap }
import akka.actor.Actor
import com.nessie.map.model.MapPoint
import com.nessie.map.ctrl.SwingBattleMapController
import com.nessie.map.model.BattleMapModifier._

trait MapView extends Actor {
	protected def generateMap(m: BattleMap): Component
	protected def select(p: MapPoint)
	protected def unselect
	import MapView._
	protected var m: BattleMap = null
	def receive: Receive = {
		case GenerateMap(m: BattleMap) =>
			this.m = m
			sender ! SwingBattleMapController.Map(generateMap(m))
			context become waitForFirstClick
	}

	var currentlySelected: MapPoint = null
	private val waitForFirstClick: Receive = {
		case CellClicked(p) if (m isOccupiedAt p)=>
			select(p)
			currentlySelected = p
			context become waitForSecondClick
	}

	private val waitForSecondClick: Receive = {
		case CellClicked(p) => if (m isOccupiedAt p) () else {
			unselect
			context become waitForFirstClick
		}
	}
	
	
}

object MapView {
	case class GenerateMap(m: BattleMap)
	case class Select(p: MapPoint)
	case class Startup
}

package com.nessie.map.ctrl

import akka.actor.{Actor, Props}
import com.nessie.map.model.{BattleMap, MapPoint}
import com.nessie.map.view.MapActor

import scala.swing.{Button, Component, Frame, MainFrame, SimpleSwingApplication}

class SwingBattleMapController(startingMap: BattleMap) extends SimpleSwingApplication with Actor {
	require(startingMap != null)

	var view: Frame = new MainFrame {
		contents = new Button("foobar")
	}
	def top = view
	val mapActor = context.actorOf(Props(new MapActor()))

	import SwingBattleMapController._

	override def receive: Receive = {
		case Startup => updateMapView(startingMap)
	}

	def updateMap(m: BattleMap): Receive = {
		case Move(src: MapPoint, dst: MapPoint) => {
			println("Hayush from " + this.self)
			updateMapView(m.move(src).to(dst))
		}
	}

	private def updateMapView(m: BattleMap) {
		context become {
			case Map(c) =>
				view.close
				view = new MainFrame {
					contents = c
				}
				startup(null)
				context become updateMap(m)
		}
		mapActor ! MapActor.GenerateMap(m)
	}
}

object SwingBattleMapController {
	case object Startup
	case class Map(c: Component)
	case class Move(src: MapPoint, dst: MapPoint) {
		def this(src: (Int, Int), dst: (Int, Int)) = this(new MapPoint(src), new MapPoint(dst))
	}
}

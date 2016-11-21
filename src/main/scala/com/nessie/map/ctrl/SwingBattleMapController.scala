package com.nessie.map.ctrl

import scala.swing.Frame
import scala.swing.SimpleSwingApplication
import java.awt.Point
import com.nessie.map.model.{ BattleMapModifier, BattleMap, MapPoint }
import com.nessie.map.view.{ MapActor, CellClicked }
import akka.actor.Actor
import akka.actor.ActorRef
import scala.swing.Component
import scala.swing.Button
import scala.swing.MainFrame
import com.nessie.map.view.MapView
import akka.actor.Props

class SwingBattleMapController(startingMap: BattleMap) extends SimpleSwingApplication with Actor {
	require(startingMap != null)

	import com.nessie.map._

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

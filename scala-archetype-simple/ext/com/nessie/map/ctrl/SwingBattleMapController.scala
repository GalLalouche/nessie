package com.nessie.map.ctrl

import scala.swing.Frame
import scala.swing.SimpleSwingApplication
import java.awt.Point
import com.nessie.map.model.{ BattleMapModifier, BattleMap, MapPoint }
import com.nessie.map.view.{ MapView, CellClicked }
import akka.actor.Actor
import akka.actor.ActorRef
import scala.swing.Component
import scala.swing.Button
import scala.swing.MainFrame

class SwingBattleMapController(m: BattleMap, mapView: ActorRef) extends SimpleSwingApplication with Actor {
	require(mapView != null)
	require(m != null)

	import com.nessie.map._

	var view: Frame = new MainFrame {
		contents = new Button("foobar")
	}
	def top = view

	//
	//		def updateMap(controller: BattleMapModifier) {
	//			ctrl = controller
	//			contents = createMapPanel
	//			controller
	//		}
	//	}
	//
	//	def handleUserClickOn(p: MapPoint) = p match {
	//		case _ if (m.isOccupiedAt(p) == false) => {
	//			val x: BattleMapModifier = m.move(currentlySelected).to(p)
	//			//			updateMap(x)
	//		}
	//	}
	//
	//	private def createMapPanel: MapView = {
	//		view = v.build(m)
	//		listenTo(view)
	//		reactions += {
	//			case CellClicked(c) if (currentlySelected != null && m.isOccupiedAt(c) == false) => {
	//				m.move(currentlySelected).to(c)
	//				top
	//			}
	//			case CellClicked(c) if (currentlySelected != null) => handleUserClickOn(c)
	//			case CellClicked(c) if (m isOccupiedAt c) => {
	//				currentlySelected = c
	//				v.select(c)
	//			}
	//			case _ => "println  fuckyou !"
	//			//			case CellClicked(c) if (currentlySelected != null && ctrl.isOccupied((c)) && c != currentlySelected) => {
	//			//				ctrl = ctrl.place(c,
	//			//					ctrl(c).asInstanceOf[HasHP].getAttacked(
	//			//						ctrl(currentlySelected).asInstanceOf[CombatUnit].getBasicAttack))
	//			//				contents = createMapPanel
	//			//			}
	//			//			case _ => {
	//			//				currentlySelected = null
	//			//				m.unselect
	//			//			}
	//		}
	//		view
	import SwingBattleMapController._
	override def receive: Receive = {
		case Startup =>
			startup(null)
			context become {
				case Map(c) =>
					view.close
					view = new MainFrame {
						contents = c
					}
					startup(null)
					context unbecome
			}
			mapView ! MapView.GenerateMap(m)
	}
}

object SwingBattleMapController {
	case class Startup
	case class Map(c: Component)
}

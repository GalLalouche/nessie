package com.nessie.map.ctrl

import scala.swing.MainFrame
import scala.swing.SimpleSwingApplication

import java.awt.Point
import com.nessie.map.model.{BattleMapModifier, BattleMap, MapPoint}
import com.nessie.map.view.{MapView, CellClicked}

class SwingBattleMapController(m: BattleMap, v: MapView) extends SimpleSwingApplication {
	require(v != null)
	require(m != null)

	import com.nessie.map._

	var currentlySelected: MapPoint = null
	//	var ctrl: BattleMap = ArrayBattleMap(5, 5).place((1, 2), new Warrior).place(2, 4, new Skeleton)
	var view: MapView = null

	def top = new MainFrame {
		location = new Point(500, 500)
		createMapPanel
		//
		//		def updateMap(controller: BattleMapModifier) {
		//			ctrl = controller
		//			contents = createMapPanel
		//			controller
		//		}
	}

	def handleUserClickOn(p: MapPoint) = p match {
		case _ if (m.isOccupied(p) == false) => {
			val x: BattleMapModifier = m.move(currentlySelected).to(p)
			//			updateMap(x)
		}
	}

	private def createMapPanel: MapView = {
		view = v.build(m)
		listenTo(view)
		reactions += {
			case CellClicked(c) if (currentlySelected != null) => handleUserClickOn(c)
			case CellClicked(c) => {
				currentlySelected = c
				v.select(c)
			}
			case _ => "println  fuckyou !"
		}
		//				case CellClicked(c) if (currentlySelected != null && ctrl.isOccupied(c) == false) => {
		//					ctrl = ctrl.move(currentlySelected).to(c)
		//					contents = createMapPanel
		//				}
		//				case CellClicked(c) if (currentlySelected != null && ctrl.isOccupied((c)) && c != currentlySelected) => {
		//					ctrl = ctrl.place(c,
		//						ctrl(c).asInstanceOf[HasHP].getAttacked(
		//							ctrl(currentlySelected).asInstanceOf[CombatUnit].getBasicAttack))
		//					contents = createMapPanel
		//				}
		//			case _ => {
		//				currentlySelected = null
		//				m.unselect
		//			}
		view
	}
}

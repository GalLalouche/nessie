package com.nessie.map.ctrl

import scala.swing.MainFrame
import scala.swing.SimpleSwingApplication

import com.nessie.units.{Skeleton, Warrior}
import java.awt.Point
import com.nessie.map.model.{BattleMapModifier, BattleMap, ArrayBattleMap, MapPoint}
import com.nessie.view.map.swing.{CellClicked, SimpleSwingBuilder, MapPanel}

object SwingBattleMapController extends SimpleSwingApplication {

	import com.nessie.map._

	var currentlySelected: MapPoint = null
	var ctrl: BattleMap = ArrayBattleMap(5, 5).place((1, 2), new Warrior).place(2, 4, new Skeleton)

	def top = new MainFrame {


		def updateMap(controller: BattleMapModifier) {
			ctrl = controller
			contents = createMapPanel
			controller
		}

		def handleUserClickOn(p: MapPoint) = p match {
			case _ if (ctrl.isOccupied(p) == false) => {
				val x: BattleMapModifier = ctrl.move(currentlySelected).to(p)
				updateMap(x)
			}
		}

		private def createMapPanel: MapPanel = {
			val m = new MapPanel(ctrl, new SimpleSwingBuilder)
			listenTo(m)
			reactions += {
				case CellClicked(c) if (currentlySelected != null) => handleUserClickOn(c)
				case CellClicked(c) => {
					currentlySelected = c
					m.select(c)
				}
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
			m
		}


		location = new Point(500, 500)
		contents = createMapPanel
	}
}

package com.nessie.view.map.swing

import scala.swing.MainFrame
import scala.swing.SimpleSwingApplication

import com.nessie.model.map.{MapPoint, ArrayBattleMap, BattleMapController}
import com.nessie.units.{CombatUnit, HasHP, Skeleton, Warrior}
import java.awt.Point

object SwingBattleMapViewer extends SimpleSwingApplication {
	var currentlySelected: MapPoint = null
	var ctrl = new BattleMapController(ArrayBattleMap(5, 5).place((1, 2), new Warrior).place(2, 4, new Skeleton))

	def top = new MainFrame {


		def updateMap(controller: BattleMapController) {
			ctrl = controller
			contents = createMapPanel
		}

		def handleUserClickOn(p: MapPoint) = {
			if (ctrl.isOccupied(p) == false)
				updateMap(ctrl move currentlySelected to p)
		}

		private def createMapPanel: MapPanel = {
			val m = new MapPanel(ctrl.map, new SimpleSwingBuilder)
			listenTo(m)
			reactions += {
				case CellClicked(c) if (currentlySelected != null) => handleUserClickOn(c)
				case CellClicked(c) if (currentlySelected != null && ctrl.isOccupied(c) == false) => {
					ctrl = ctrl.move(currentlySelected).to(c)
					contents = createMapPanel
				}
				case CellClicked(c) if (currentlySelected != null && ctrl.isOccupied((c)) && c != currentlySelected) => {
					ctrl = ctrl.place(c,
						ctrl(c).asInstanceOf[HasHP].getAttacked(
							ctrl(currentlySelected).asInstanceOf[CombatUnit].getBasicAttack))
					contents = createMapPanel
				}
				case _ => {
					currentlySelected = null
					m.unselect
				}
			}
			m
		}

		location = new Point(500, 500)
		contents = createMapPanel
	}
}
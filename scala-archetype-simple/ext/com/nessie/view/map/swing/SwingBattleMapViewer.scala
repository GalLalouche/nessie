package com.nessie.view.map.swing

import scala.swing.MainFrame
import scala.swing.SimpleSwingApplication

import com.nessie.model.map.{MapPoint, ArrayBattleMap, BattleMapController}
import com.nessie.units.Skeleton
import com.nessie.units.Warrior

object SwingBattleMapViewer extends SimpleSwingApplication {
	var ctrl = new BattleMapController(ArrayBattleMap(3, 5).place((1, 2), new Warrior).place(2, 4, new Skeleton))


	def top = new MainFrame {

		private def createMapPanel: MapPanel = {
			val m = new MapPanel(ctrl.map, new SimpleSwingBuilder)
			listenTo(m)
			var currentlySelected: MapPoint = null
			reactions += {
				case CellClicked(c) if (ctrl.isOccupied(c)) => {
					m.unselect(currentlySelected)
					currentlySelected = c
					m.select(c)
				}
				case CellClicked(c) => if (currentlySelected != null && ctrl.isOccupied(c) == false) {
					ctrl = ctrl.move(currentlySelected).to(c)
					contents = createMapPanel
				}
				case _ => {
					m.unselect
				}
			}
			m
		}

		contents = createMapPanel
	}

}
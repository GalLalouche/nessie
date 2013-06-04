package com.nessie.view.map.swing

import scala.swing.MainFrame
import scala.swing.SimpleSwingApplication

import com.nessie.model.map.{MapPoint, ArrayBattleMap, BattleMapController}
import com.nessie.units.Skeleton
import com.nessie.units.Warrior

object SwingBattleMapViewer extends SimpleSwingApplication {
	val map = ArrayBattleMap(3, 5).place((1, 2), new Warrior).place(2, 4, new Skeleton)
	val ctrl = new BattleMapController(map)


	def top = new MainFrame {
		createMapPanel

		private def createMapPanel: MapPanel = {
			val m = new MapPanel(map, new SimpleSwingBuilder)
			contents = m;
			listenTo(m)
			var currentlySelected: MapPoint = new MapPoint(0, 0)
			reactions += {
				case CellClicked(c) if (ctrl.isOccupied(c)) => {
					m.unselect(currentlySelected)
					currentlySelected = c
					m.select(c)
				}
				case CellClicked(c) => if (currentlySelected != null) {
					ctrl.move(currentlySelected).to(c)
					contents = createMapPanel
				}
			}
			m
		}
	}

}
package com.nessie.view.map

import scala.swing.MainFrame
import scala.swing.SimpleSwingApplication

import com.nessie.model.map.ArrayBattleMap
import com.nessie.model.map.BattleMapController
import com.nessie.units.Skeleton
import com.nessie.units.Warrior

object HelloWorld extends SimpleSwingApplication {
	val map = new ArrayBattleMap(3, 5)
	map(1, 2) = new Warrior
	map(2, 4) = new Skeleton
	val ctrl = new BattleMapController(map)
	def top = new MainFrame {
		val m =new MapPanel(map)
		contents = m;
		listenTo(m)
		reactions += {
			case CellClicked(c) => println(c + " clicked")  
		}
	}
}
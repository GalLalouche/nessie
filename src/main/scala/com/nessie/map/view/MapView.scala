package com.nessie.map.view

import com.nessie.map.model.BattleMap
import com.nessie.map.model.MapPoint

import scala.swing.GridPanel

trait MapView {
	def generateMap(m: BattleMap): GridPanel
	def select(p: MapPoint)
	def unselect
}

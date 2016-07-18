package com.nessie.map.view

import scala.swing.Component

import com.nessie.map.model.BattleMap
import com.nessie.map.model.MapPoint

trait MapView {
	def generateMap(m: BattleMap): Component
	def select(p: MapPoint)
	def unselect
}

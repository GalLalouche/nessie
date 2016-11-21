package com.nessie.map.view

import com.nessie.map.model.BattleMap
import com.nessie.map.model.MapPoint

trait MapView {
	def generateMap(m: BattleMap)
	def select(p: MapPoint)
	def unselect
}

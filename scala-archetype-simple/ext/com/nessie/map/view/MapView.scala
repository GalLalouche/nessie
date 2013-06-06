package com.nessie.map.view

import scala.swing.Publisher
import com.nessie.map.model.BattleMap

trait MapView extends Publisher {
	def build(m: BattleMap): MapView
}

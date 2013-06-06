package com.nessie.map.view

import scala.swing.{Component, Publisher}
import com.nessie.map.model.{MapPoint, BattleMap}

trait MapView extends Component with Publisher {
	def build(m: BattleMap): MapView

	def select(p: MapPoint)
}

package com.nessie.map.view

import com.nessie.map.BattleMapObject

import scala.swing.Component

trait SwingBuilder {
	def apply(o: BattleMapObject): Component
}

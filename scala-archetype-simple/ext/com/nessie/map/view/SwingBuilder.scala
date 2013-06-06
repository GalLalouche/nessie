package com.nessie.map.view

import com.nessie.model.map.objects.BattleMapObject
import scala.swing.Component

trait SwingBuilder {
	def apply(o: BattleMapObject): Component
}

package com.nessie.map.view.swing

import com.nessie.model.map.objects.{EmptyMapObject, BattleMapObject}
import scala.swing.{Component, Button}
import com.nessie.units.{Skeleton, Warrior}
import java.awt.Dimension
import com.nessie.map.view.SwingBuilder
import com.nessie.map.objects.CombatUnitObject
import com.nessie.units.CombatUnit

class SimpleSwingBuilder extends SwingBuilder {
	def apply(o: BattleMapObject): Component = new Button(string(o)) {
		val s = new Dimension(75, 75)
		minimumSize = s
		maximumSize = s
		preferredSize = s
	}

	private def string(o: BattleMapObject): String = o match {
		case EmptyMapObject => "_"
		case CombatUnitObject(c: CombatUnit) => c.getClass.getSimpleName().take(2)
	}
}

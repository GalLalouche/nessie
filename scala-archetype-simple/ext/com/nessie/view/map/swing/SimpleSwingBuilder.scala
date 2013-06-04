package com.nessie.view.map.swing

import com.nessie.model.map.objects.{EmptyMapObject, BattleMapObject}
import scala.swing.{Action, Component, Button}
import com.nessie.units.{Skeleton, Warrior}

class SimpleSwingBuilder extends SwingBuilder {
	def apply(o: BattleMapObject): Component = new Button(string(o))

	private def string(o: BattleMapObject) = o match {
		case EmptyMapObject => "_"
		case _: Warrior => "w"
		case _: Skeleton => "s"
	}
}

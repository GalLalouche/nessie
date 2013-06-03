package com.nessie.view.map

import scala.swing.GridPanel
import com.nessie.model.map.BattleMap
import scala.swing.Button
import scala.swing.event.ButtonClicked
import com.nessie.model.map.objects.BattleMapObject
import com.nessie.units.Warrior
import com.nessie.model.map.objects.EmptyMapObject
import com.nessie.units.Skeleton

class MapPanel(map: BattleMap) extends GridPanel(map.width, map.height) {
	contents ++= map map (x => {
		val b = new Button(print(x._3));
		listenTo(b)
		b
	})

	private def print(o: BattleMapObject) = o match {
		case EmptyMapObject => "_"
		case _: Warrior => "w"
		case _: Skeleton => "s"
	}
}
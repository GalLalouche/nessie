package com.nessie.view.map.swing

import scala.swing.GridPanel
import com.nessie.model.map.BattleMap
import scala.swing.Button
import scala.swing.event.ButtonClicked
import com.nessie.model.map.objects.BattleMapObject
import com.nessie.units.Warrior
import com.nessie.model.map.objects.EmptyMapObject
import com.nessie.units.Skeleton
import scala.swing.Publisher
import scala.swing.Action
import com.nessie.model.map.MapPoint
import scala.swing.event.Event
import scala.swing.event.ActionEvent

case class CellClicked(p:MapPoint) extends Event
class MapPanel(map: BattleMap) extends GridPanel(map.width, map.height) {
	contents ++= map map (x => {
		val b = new Button(Action(print(x._2)) {
			publish(CellClicked(x._1))
		});
		listenTo(b)
		b
	})

	private def print(o: BattleMapObject) = o match {
		case EmptyMapObject => "_"
		case _: Warrior => "w"
		case _: Skeleton => "s"
	}
}
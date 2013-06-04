package com.nessie.view.map.swing

import scala.swing.GridPanel
import com.nessie.model.map.BattleMap
import scala.swing.Button
import com.nessie.model.map.objects.BattleMapObject
import com.nessie.units.Warrior
import com.nessie.model.map.objects.EmptyMapObject
import com.nessie.units.Skeleton
import scala.swing.Action
import com.nessie.model.map.MapPoint
import scala.swing.event.Event

case class CellClicked(p: MapPoint) extends Event {
	override def equals(obj: Any) = obj.isInstanceOf[CellClicked] && obj.asInstanceOf[CellClicked].p == p

	override def hashCode(): Int = p.hashCode
}

class MapPanel(map: BattleMap) extends GridPanel(map.width, map.height) {
	require(map != null)
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

	override def toString = "MapPanel"
}
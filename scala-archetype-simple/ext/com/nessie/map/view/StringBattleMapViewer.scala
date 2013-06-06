package com.nessie.map.view

import com.nessie.model.map.objects.BattleMapObject
import com.nessie.model.map.objects.EmptyMapObject
import com.nessie.map.model.BattleMap

class StringBattleMapViewer(m: BattleMap) {
	override def toString() = {
		val sb = new StringBuilder
		for (c <- m) {
			sb append print(c._2)
			if (c._1.x != m.width - 1) sb append ","
			else sb append "\n"
		}
		sb toString
	}

	private def print(o: BattleMapObject) = o match {
		case EmptyMapObject => "_"
	}
}
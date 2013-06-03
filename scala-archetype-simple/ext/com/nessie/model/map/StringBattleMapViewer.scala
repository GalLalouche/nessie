package com.nessie.model.map

class StringBattleMapViewer(m: BattleMap) {
	override def toString() = {
		val sb = new StringBuilder
		for (c <- m) {
			sb append print(c._3)
			if (c._1 != m.width - 1) sb append ","
			else sb append "\n"
		}
		sb toString
	}
	
	private def print(o: BattleMapObject) = o match {
		case EmptyMapObject => "_"
	}
}
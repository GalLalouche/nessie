package com.nessie.view.map

import scala.swing._
import com.nessie.model.map.ArrayBattleMap
import com.nessie.model.map.objects.BattleMapObject
import com.nessie.model.map.objects.EmptyMapObject

object HelloWorld extends SimpleSwingApplication {
	val map = new ArrayBattleMap(3, 5)
	def top = new MainFrame {
		contents = new GridPanel(map.width, map.height) {
			contents ++= map map (n=> new Button(print(n._3)))
		}
	}
	
	private def print(o: BattleMapObject) = o match {
		case EmptyMapObject => "_"
	}
}
package com.nessie

import com.nessie.map.model.ArrayBattleMap
import com.nessie.map.view.StringBattleMapViewer

object Demo {
	val map = ArrayBattleMap(5, 5)
	def main(args: Array[String]) {
		println(new StringBattleMapViewer(map).toString())
	}
}

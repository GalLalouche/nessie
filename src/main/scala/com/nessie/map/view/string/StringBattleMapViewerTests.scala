package com.nessie.map.view.string

import com.nessie.map.model.ArrayBattleMap
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{BeforeAndAfter, FlatSpec}

class StringBattleMapViewerTests extends FlatSpec with ShouldMatchers with BeforeAndAfter {
	"StringViewer" should "print all _ for empty map" in {
		val map = ArrayBattleMap(2, 3)
		val $ = new StringBattleMapViewer(map)
		$.toString should be ===
			"_,_\n" +
			"_,_\n" +
			"_,_\n"
	}
}


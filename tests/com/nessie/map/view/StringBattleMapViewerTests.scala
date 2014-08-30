package com.nessie.map.view

import org.scalamock.scalatest.MockFactory
import org.scalatest.BeforeAndAfter
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

import com.nessie.map.model.ArrayBattleMap

class StringBattleMapViewerTests extends FlatSpec with ShouldMatchers with BeforeAndAfter {
	"StringViewer" should "print all _ for empty map" in {
		val map = ArrayBattleMap(2, 3);
		val $ = new StringBattleMapViewer(map)
		$.toString should be ===
			"_,_\n" +
			"_,_\n" +
			"_,_\n";
	}
}


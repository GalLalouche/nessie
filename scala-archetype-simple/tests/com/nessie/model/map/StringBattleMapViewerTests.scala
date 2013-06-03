package com.nessie.model.map

import scala.collection.GenTraversable

import org.scalamock.scalatest.MockFactory
import org.scalatest.BeforeAndAfter
import org.scalatest.FlatSpec
import org.scalatest.matchers._

class StringBattleMapViewerTests extends FlatSpec with ShouldMatchers with MockFactory with BeforeAndAfter {
	var $: BattleMap = new ArrayBattleMap(5, 10)
	private def mockObject = mock[BattleMapObject]
	val o = mockObject
	val emptyP = (0, 1)
	val occupiedP = (1, 0)
	before({
		$ = new ArrayBattleMap(5, 10)
		$(occupiedP) = mockObject
	})

	"StringViewer" should "print all _ for empty map" in {
		val map = new ArrayBattleMap(2, 3);
		val $ = new StringBattleMapViewer(map)
		$.toString should be ===
			"_,_\n" +
			"_,_\n" +
			"_,_\n";
	}
}


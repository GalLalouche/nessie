package com.nessie.model.map

import org.scalamock.scalatest.MockFactory
import org.scalatest.{OneInstancePerTest, FlatSpec}
import org.scalatest.matchers.ShouldMatchers
import com.nessie.model.map.objects.BattleMapObject

class BattleMapControllerTests extends FlatSpec with ShouldMatchers with MockFactory with OneInstancePerTest {
	private def mockObject = mock[BattleMapObject]

	val o = mockObject

	val emptyP = (0, 1)
	val occupiedP = (1, 0)

	var $: BattleMapController = new BattleMapController(ArrayBattleMap(5, 10)).place(occupiedP, mockObject)

	"Apply" should "throw an exception on empty apply" in {
		evaluating {
			$(emptyP)
		} should produce[MapEmptyException]
	}

	it should "also work with an (Int, Int) parameter)" in {
		val (x, y) = emptyP
		$ = $.place(emptyP, o)
		$(emptyP) should be === o
	}

	"Update" should "place an object" in {
		$.place(emptyP, o)(emptyP) should be === o
	}

	it should "throw an exception on non-empty" in {
		$ = $.place(emptyP, o)
		evaluating($.place(emptyP, o)) should produce[MapOccupiedException]
	}

	"IsOccupied" should "return false on empty slot" in {
		$.isOccupied(emptyP) should be === false
	}

	it should "return true on occupied slot" in {
		$.isOccupied(occupiedP) should be === true
	}

	"Remove" should "throw exception on unoccupied cell" in {
		evaluating {
			$.remove(emptyP)
		} should produce[MapEmptyException]
	}

	"Move" should "throw exception on unoccupied source" in {
		evaluating {
			$.move(emptyP).to(occupiedP)
		} should produce[MapEmptyException]
	}

	it should "throw an exception on occupied destination" in {
		evaluating {
			$.place(emptyP, o).move(emptyP).to(occupiedP)
		} should produce[MapOccupiedException]
	}

	it should "move the object" in {
		$ = $.remove(occupiedP).place(occupiedP, o).move(occupiedP).to(emptyP)
		$(emptyP) should be === o
		$.isOccupied(occupiedP) should be === false
	}
}


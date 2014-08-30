package com.nessie.map.model

import org.scalatest.mock.MockitoSugar
import org.scalatest.{OneInstancePerTest, FlatSpec}
import org.scalatest.matchers.ShouldMatchers
import com.nessie.model.map.objects.BattleMapObject
import com.nessie.map.exceptions.{MapOccupiedException, MapEmptyException}

class BattleMapModifierTests extends FlatSpec with ShouldMatchers with OneInstancePerTest with MockitoSugar {
	private def mockObject = mock[BattleMapObject]

	val o = mockObject

	val emptyP = (0, 1)
	val occupiedP = (1, 0)

	var $: BattleMap = ArrayBattleMap(5, 10).place(occupiedP, o)

	it should "also work with an (Int, Int) parameter)" in {
		val (x, y) = emptyP
		$ = $.place(emptyP, o)
		$(emptyP) should be === o
	}

	"Update" should "place an object" in {
		$.place(emptyP, o)(emptyP) should be === o
	}

	"IsOccupied" should "return false on empty slot" in {
		$.isOccupiedAt(emptyP) should be === false
	}

	it should "return true on occupied slot" in {
		$.isOccupiedAt(occupiedP) should be === true
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
		$.isOccupiedAt(occupiedP) should be === false
		$(emptyP) should be === o
	}
}


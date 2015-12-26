package com.nessie.map.model

import com.nessie.map.exceptions.{MapEmptyException, MapOccupiedException}
import com.nessie.model.map.objects.BattleMapObject
import org.scalatest.mock.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}

class BattleMapModifierTest extends FlatSpec with MockitoSugar with Matchers {
	private def mockObject = mock[BattleMapObject]

	val o = mockObject

	val emptyP = (0, 1)
	val occupiedP = (1, 0)

	var $: BattleMap = ArrayBattleMap(5, 10).place(occupiedP, o)

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
		a[MapEmptyException] should be thrownBy {
			$.remove(emptyP)
		}
	}

	"Move" should "throw exception on unoccupied source" in {
		a[MapEmptyException] should be thrownBy {
			$.move(emptyP).to(occupiedP)
		}
	}

	it should "throw an exception on occupied destination" in {
		a[MapOccupiedException] should be thrownBy {
			$.place(emptyP, o).move(emptyP).to(occupiedP)
		}
	}

	it should "move the object" in {
		$ = $.remove(occupiedP).place(occupiedP, o).move(occupiedP).to(emptyP)
		$.isOccupiedAt(occupiedP) should be === false
		$(emptyP) should be === o
	}
}




package com.nessie.model.map

import org.scalamock.scalatest.MockFactory
import org.scalatest.BeforeAndAfter
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import com.nessie.model.map.objects.BattleMapObject



class BattleMapControllerTests extends FlatSpec with ShouldMatchers with MockFactory with BeforeAndAfter {
	import BattleMap.tupleToMapPoint
	var $: BattleMapController = null
	private def mockObject = mock[BattleMapObject]
	val o = mockObject
	val emptyP = (0, 1)
	val occupiedP = (1, 0)
	
	before({
		$ = new BattleMapController(new ArrayBattleMap(5, 10));
		$(occupiedP) = mockObject
	})
	
	"Apply" should "throw an exception on empty apply" in {
		evaluating { $(emptyP) } should produce[MapEmptyException]
	}

	it should "also work with an (Int, Int) parameter)" in {
		val (x, y) = emptyP
		$(emptyP) = o
		$(emptyP) should be === o
	}

	"Update" should "place an object" in {
		$(emptyP) = o
		$(emptyP) should be === o
	}

	it should "throw an exception on non-empty" in {
		$(emptyP) = o
		evaluating($(emptyP) = o) should produce[MapOccupiedException]
	}

	"IsOccupied" should "return false on empty slot" in {
		$.isOccupied(emptyP) should be === false
	}

	it should "return true on occupied slot" in {
		$.isOccupied(occupiedP) should be === true
	}

	"Remove" should "throw exception on unoccupied cell" in {
		evaluating { $.remove(emptyP) } should produce[MapEmptyException]
	}

	it should "return the removed object" in {
		$(emptyP) = o
		$.remove(emptyP) should be === o
	}

	"Move" should "throw exception on unoccupied source" in {
		evaluating { $.move(emptyP).to(occupiedP) } should produce[MapEmptyException]
	}

	it should "throw an exception on occupied destination" in {
		$(emptyP) = o
		evaluating { $.move(emptyP).to(occupiedP) } should produce[MapOccupiedException]
	}
	
	it should "move the object" in {
		$.remove(occupiedP)
		$(occupiedP) = o
		$.move(occupiedP).to(emptyP)
		$(emptyP) should be === o
		$.isOccupied(occupiedP) should be === false
	}
	
	it should "not remove the source if dst isn't clear" in {
		$(emptyP) = o
		evaluating { $.move(occupiedP).to(emptyP) } should produce[MapOccupiedException]
		$.isOccupied(occupiedP) should be === true
		$(emptyP) should be === o
		
	}
	
	it should "should not place the object if the destination is occupied" in {
		$(emptyP) = o
		evaluating { $.move(emptyP).to(occupiedP) } should produce[MapOccupiedException]
		$(emptyP) should be === o
	}
}


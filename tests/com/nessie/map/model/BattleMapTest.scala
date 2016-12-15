package com.nessie.model.map.model

import com.nessie.model.map._
import org.scalatest.mock.MockitoSugar

import scala.collection.GenTraversable
import org.scalatest.{FlatSpec, OneInstancePerTest}
import org.scalatest.matchers._
import com.nessie.model.map.objects.BattleMapObject
import com.nessie.model.map.objects.EmptyMapObject

trait CustomMatchers extends ShouldMatchers {
	def forAll[T](right: T => Boolean) = new Matcher[GenTraversable[T]] {
		override def apply(left: GenTraversable[T]) = {
			val leftPretty = left.take(3) + {
				if (left.size > 3) "..." else ""
			}
			MatchResult(left.forall(right),
				right + " does not apply to all of " + leftPretty,
				right + " applies too all of " + leftPretty)
		}
	}
}

class BattleMapTest extends FlatSpec with CustomMatchers with OneInstancePerTest with MockitoSugar {
	private def mockObject = mock[BattleMapObject]

	val o = mockObject

	val emptyP = (0, 1)
	val occupiedP = (1, 0)

	var $: BattleMap = ArrayBattleMap(5, 10).place(occupiedP, mockObject)

	"Constructor" should "return the correct height and width" in {
		$ = ArrayBattleMap(5, 10)
		$.width should be === 5
		$.height should be === 10
	}

	it should "throw IllegalArgumentException on negative height" in {
		an[IllegalArgumentException] should be thrownBy {
			ArrayBattleMap(-3, 10)
		}
	}

	it should "should be of size width*height" in {
		$.size should be === 50
	}

	it should "start out as all empty" in {
		$ = ArrayBattleMap(10, 20)
		$ should forAll[(MapPoint, BattleMapObject)](x => x._2 == EmptyMapObject);
	}

	"Update" should "place an object" in {
		$.place(emptyP, o)(emptyP) should be === o
	}

	"row" should "return the nth row" in {
		$ = ArrayBattleMap(5, 5).place((2, 3), o)
		$ row 3 should be === List.fill(5)(EmptyMapObject).updated(2, o)
		$ row 2 should be === List.fill(5)(EmptyMapObject)
	}

	"column" should "return the nth column" in {
		$ = (ArrayBattleMap(5, 5)).place((2, 3), o)
		$ column 2 should be === List.fill(5)(EmptyMapObject).updated(3, o)
		$ column 3 should be === List.fill(5)(EmptyMapObject)
	}

	"rows" should "returns all the rows" in {
		$ = (ArrayBattleMap(5, 5)).place((2, 3), o)
		$ rows 3 should be === List.fill(5)(EmptyMapObject).updated(2, o)
	}

	"columns" should "returns all the columns" in {
		$ = (ArrayBattleMap(5, 5)).place((2, 3), o)
		$ columns 2 should be === List.fill(5)(EmptyMapObject).updated(3, o)
	}
}


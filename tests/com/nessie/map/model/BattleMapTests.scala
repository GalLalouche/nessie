package com.nessie.map.model

import org.scalatest.mock.MockitoSugar

import scala.collection.GenTraversable
import org.scalamock.scalatest.MockFactory
import org.scalatest.{ OneInstancePerTest, FlatSpec }
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

class BattleMapTests extends FlatSpec with ShouldMatchers with CustomMatchers with OneInstancePerTest with MockitoSugar {
	private def mockObject = mock[BattleMapObject]

	val o = mockObject

	val emptyP = (0, 1)
	val occupiedP = (1, 0)

	var $: BattleMap = ArrayBattleMap(5, 10).place(occupiedP, mockObject)

	"Constructor" should "return the correct height and width" in {
		$ = ArrayBattleMap(5, 10);
		$.width should be === 5
		$.height should be === 10
	}

	it should "throw IllegalArgumentException on negative height" in {
		evaluating {
			ArrayBattleMap(-3, 10)
		} should produce[IllegalArgumentException]
	}

	it should "should be of size width*height" in {
		$.size should be === 50
	}

	"Apply" should "throw IllegalArgumentException on zero width" in {
		evaluating {
			ArrayBattleMap(5, 0)
		} should produce[IllegalArgumentException]
	}

	it should "should throw exception on negative apply" in {
		evaluating {
			$(-3, 10)
		} should produce[IllegalArgumentException]
	}

	it should "should throw exception on to large of apply" in {
		evaluating {
			$(0, 10)
		} should produce[IndexOutOfBoundsException]
	}

	it should "start out as all empty" in {
		$ = ArrayBattleMap(10, 20)
		$ should forAll[(MapPoint, BattleMapObject)](x => x._2 == EmptyMapObject);
	}

	"Update" should "place an object" in {
		$.place(emptyP, o)(emptyP) should be === o
	}

	"row" should "throw exception on negative value" in {
		evaluating { $ row -4 } should produce[IllegalArgumentException]
	}

	it should "throw exception on value that is larger than width" in {
		evaluating { $ row $.height } should produce[IndexOutOfBoundsException]
	}

	it should "return the nth row" in {
		$ = (ArrayBattleMap(5, 5)).place((2, 3), o)
		$ row 3 should be === List.fill(5)(EmptyMapObject).updated(2, o)
		$ row 2 should be === List.fill(5)(EmptyMapObject)
	}

	"column" should "throw exception on negative value" in {
		evaluating { $ column -1 } should produce[IllegalArgumentException]
	}

	it should "throw exception on value that is larger than width" in {
		evaluating { $ column $.width } should produce[IndexOutOfBoundsException]
	}

	it should "return the nth column" in {
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


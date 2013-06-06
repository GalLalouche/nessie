package com.nessie.map.model

import scala.collection.GenTraversable
import org.scalamock.scalatest.MockFactory
import org.scalatest.{OneInstancePerTest, FlatSpec}
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

class BattleMapTests extends FlatSpec with ShouldMatchers with MockFactory with CustomMatchers with OneInstancePerTest {
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

	it should "should throw exception on too large of apply" in {
		evaluating {
			$(0, 10)
		} should produce[IndexOutOfBoundsException]
	}

	it should "start out as all empty" in {
		$ = ArrayBattleMap(10, 20)
		$ should forAll[(MapPoint, BattleMapObject)](x => x._2 == EmptyMapObject);
	}

	"Update" should "place an object" in {
		$.place(emptyP, o)
		$.place(emptyP, o)(emptyP) should be === o
	}
}


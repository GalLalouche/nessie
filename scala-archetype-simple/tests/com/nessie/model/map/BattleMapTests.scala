package com.nessie.model.map

import scala.collection.GenTraversable
import org.scalamock.scalatest.MockFactory
import org.scalatest.BeforeAndAfter
import org.scalatest.FlatSpec
import org.scalatest.matchers._
import com.nessie.model.map.objects.BattleMapObject
import com.nessie.model.map.objects.EmptyMapObject

trait CustomMatchers extends ShouldMatchers {
	def forAll[T](right: T => Boolean) = new Matcher[GenTraversable[T]] {
		override def apply(left: GenTraversable[T]) = {
			val leftPretty = left.take(3) + { if (left.size > 3) "..." else "" }
			MatchResult(left.forall(right),
				right + " does not apply to all of " + leftPretty,
				right + " applies too all of " + leftPretty)
		}
	}
}

class BattleMapTests extends FlatSpec with ShouldMatchers with MockFactory with CustomMatchers with BeforeAndAfter {
	var $: BattleMap = new ArrayBattleMap(5, 10)
	private def mockObject = mock[BattleMapObject]
	val o = mockObject
	val emptyP = (0, 1)
	val occupiedP = (1, 0)
	before({
		$ = new ArrayBattleMap(5, 10)
		$(occupiedP) = mockObject
	})

	"Contructor" should "return the correct height and width" in {
		$ = new ArrayBattleMap(5, 10);
		$.width should be === 5
		$.height should be === 10
	}

	it should "throw IllegalArgumentException on negative height" in {
		evaluating { new ArrayBattleMap(-3, 10) } should produce[IllegalArgumentException]
	}

	it should "should be of size width*height" in {
		$.size should be === 50
	}

	"Apply" should "throw IllegalArgumentException on zero width" in {
		evaluating { new ArrayBattleMap(5, 0) } should produce[IllegalArgumentException]
	}

	it should "should throw exception on negative apply" in {
		evaluating { $(-3, 10) } should produce[IndexOutOfBoundsException]
	}

	it should "should throw exception on too large of apply" in {
		evaluating { $(0, 10) } should produce[IndexOutOfBoundsException]
	}

	it should "start out as all empty" in {
		$ = new ArrayBattleMap(10, 20)
		$ should forAll[(Int, Int, BattleMapObject)](x => x._3 == EmptyMapObject);
	}

	"Update" should "place an object" in {
		$(emptyP) = o
		$(emptyP) should be === o
	}
}


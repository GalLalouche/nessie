package com.nessie.model.map

import org.scalatest.FlatSpec
import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers._
import scala.collection.GenTraversable
import org.scalatest.BeforeAndAfter

trait CustumMatchers extends ShouldMatchers {
	def forAll[T](right: T => Boolean) = new Matcher[GenTraversable[T]] {
		override def apply(left: GenTraversable[T]) = {
			val leftPretty = left.take(3) + { if (left.size > 3) "..." else "" }
			MatchResult(left.forall(right),
				right + " does not apply to all of " + leftPretty,
				right + " applies too all of " + leftPretty)
		}
	}
}

class BattleMapTests extends FlatSpec with ShouldMatchers with MockFactory with CustumMatchers with BeforeAndAfter {
	var $: BattleMap = new ArrayBattleMap(5, 10)
	private def mockObject = mock[BattleMapObject]
	val o = mockObject
	val p = (0, 1)
	before($ = new ArrayBattleMap(5, 10))

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
		$ should forAll[(Int, Int, BattleMapObject)](x => x._3 == EmptyMapObject);
	}

	it should "throw an exception on empty apply" in {
		evaluating { $(p) } should produce[IllegalStateException]
	}
	it should "also work with an (Int, Int) parameter)" in {
		val (x, y) = p
		$(x, y) = o
		$(p) should be === o
	}

	"Update" should "place an object" in {
		$(0, 0) = o
		$(0, 0) should be === o
	}

	it should "untuple the point parameter correctly" in {
		$(3, 5) = o;
		$((3, 5)) should be === o
		val m2 = mockObject
		$((4, 2)) = m2
		$(4, 2) should be === m2
	}

	"IsOccupied" should "return false on empty slot" in {
		$.isOccupied(0, 0) should be === false
	}

	it should "return true on occupied slot" in {
		$(0, 0) = o;
		$.isOccupied(0, 0) should be === true
	}

	"Remove" should "throw exception on unoccupied cell" in {
		evaluating($.remove(0, 0)) should produce[IllegalStateException]
	}

	it should "return the removed object" in {
		$(0, 0) = o
		$.remove(0, 0) should be === o
	}

	"Move" should "throw exception on unoccupied cell" in {
		evaluating($.move(0, 0).to(1, 1)) should produce[IllegalStateException]
	}
}


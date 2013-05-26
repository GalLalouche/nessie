package com.nessie.model.map

import org.scalatest.FlatSpec
import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers._
import scala.collection.GenTraversable

trait CustumMatchers extends ShouldMatchers {
	def forAll[T](right: T => Boolean) = new Matcher[GenTraversable[T]] {
		override def apply(left: GenTraversable[T]) = {
					val leftPretty = left.take(3) + {if (left.size > 3) "..." else ""}
					MatchResult(left.forall(right),
					right + " does not apply to all of " + leftPretty,
					right + " applies too all of " + leftPretty)
		}
	}
}

class BattleMapTests extends FlatSpec with ShouldMatchers with MockFactory with CustumMatchers {
	var $: BattleMap = new ArrayBattleMap(5, 10)
	"Map Contructor" should "return the correct height and width" in {
		$ = new ArrayBattleMap(5, 10);
		$.width should be === 5
		$.height should be === 10
	}

	private def mockObject = mock[BattleMapObject]

	it should "throw IllegalArgumentException on negative height" in {
		evaluating { new ArrayBattleMap(-3, 10) } should produce[IllegalArgumentException]
	}

	it should "should be of size width*height" in {
		$.size should be === 50
	}

	"Map Apply" should "throw IllegalArgumentException on zero width" in {
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
	
	it should "also work with an (Int, Int) parameter)" in {
		$((0,0)) should be === EmptyMapObject
	}

	"Map update" should "place an object" in {
		val m = mockObject
		$(0, 0) = m
		$(0, 0) should be === m
	}
	
	it should "untuple the point parameter correctly" in { 
		val m = mockObject;
		$(3, 5) = m;
		$((3, 5)) should be === m
		val m2 = mockObject
		$((4, 2)) = m2
		$(4, 2) should be === m2
	}
		
}



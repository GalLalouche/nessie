package com.nessie.map.model

import com.nessie.map.{BattleMapObject, EmptyMapObject}

import scala.collection.GenTraversable
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, OneInstancePerTest}
import org.scalatest.matchers._

class MapPointTests extends FlatSpec with ShouldMatchers {
	"equals" should "return true when x and y are equal" in {
		new MapPoint(0, 0) should be === new MapPoint(0, 0)
	}

	it should "return false when x is different and y is equal" in {
		MapPoint(0, 0) should not be MapPoint(1, 0)
	}
	
	it should "return false when y is different and x is equal" in {
		MapPoint(0, 0) should not be MapPoint(0, 1)
	}
	
	it should "return false when both x and y are different" in {
		MapPoint(0, 0) should not be MapPoint(1, 1)
	}
	
	it should "work with tuples" in {
		MapPoint(0, 0) should be === (0,0)
	}
}


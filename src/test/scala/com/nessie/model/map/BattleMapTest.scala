package com.nessie.model.map

import common.AuxSpecs
import common.rich.func.MoreIterableInstances
import org.scalatest.FreeSpec
import scalax.collection.GraphEdge.UnDiEdge

import scalaz.syntax.ToFunctorOps

abstract class BattleMapTest extends FreeSpec with AuxSpecs
    with ToFunctorOps with MoreIterableInstances {
  protected def createBattleMap(width: Int, height: Int): BattleMap

  "Constructor" - {
    "Negative width" in {an[IllegalArgumentException] should be thrownBy createBattleMap(-3, 10)}
    "Zero width" in {an[IllegalArgumentException] should be thrownBy createBattleMap(0, 10)}
    "Negative height" in {an[IllegalArgumentException] should be thrownBy createBattleMap(3, -10)}
    "Zero height" in {an[IllegalArgumentException] should be thrownBy createBattleMap(3, 0)}
    "starts out as all empty" in {DictBattleMap(10, 20).objects.map(_._2).forall(_ == EmptyMapObject) shouldReturn true}
  }
  "Attributes" in {
    val $ = createBattleMap(width = 5, height = 10)
    $.width shouldReturn 5
    $.height shouldReturn 10
    $.size shouldReturn 50
  }
  "Exceptions" - {
    val objectPoint = MapPoint(0, 0)
    val emptyPoint = MapPoint(3, 4)
    val betweenPoints = DirectionalMapPoint(objectPoint, Direction.Left)
    val emptyBetween = DirectionalMapPoint(1, 2, Direction.Up)
    val $ = createBattleMap(5, 2)
        .place(objectPoint, NonEmptyBattleMapObject)
        .place(betweenPoints, NonEmptyBetweenMapObject)
    "Out of bounds" - { // Negatives indices handled by the appropriate case class constructor
      "Object" in {
        an[IndexOutOfBoundsException] should be thrownBy $.place(MapPoint(5, 1), NonEmptyBattleMapObject)
        an[IndexOutOfBoundsException] should be thrownBy $.place(MapPoint(1, 2), NonEmptyBattleMapObject)
      }
      "Between" in {
        an[IndexOutOfBoundsException] should be thrownBy $.place(DirectionalMapPoint(5, 1, Direction.Down), NonEmptyBetweenMapObject)
        an[IndexOutOfBoundsException] should be thrownBy $.place(DirectionalMapPoint(1, 2, Direction.Down), NonEmptyBetweenMapObject)
      }
    }
    "Place on non-empty" - {
      "Object" in {an[MapOccupiedException] should be thrownBy $.place(objectPoint, NonEmptyBattleMapObject)}
      "Between" in {an[MapOccupiedException] should be thrownBy $.place(betweenPoints, NonEmptyBetweenMapObject)}
    }
    "Place empty" - {
      "Object" in {an[IllegalArgumentException] should be thrownBy $.place(emptyPoint, EmptyMapObject)}
      "Between" in {an[IllegalArgumentException] should be thrownBy $.place(emptyBetween, EmptyBetweenMapObject)}
    }
    "Remove empty" - {
      "Object" in {a[MapEmptyException] should be thrownBy $.remove(MapPoint(1, 1))}
      "Between" in {a[MapEmptyException] should be thrownBy $.remove(DirectionalMapPoint(0, 0, Direction.Down))}
    }
  }
  "betweens" - {
    "sizes" in {
      for (w <- 1 to 10; h <- 1 to 10) {
        val map = createBattleMap(width = w, height = h)
        val expectedBetweenSize = w * 2 + h * 2 + (w - 1) * h + (h - 1) * w
        val actualSize = map.betweenObjects.map(_._1).toSet.size
        if (actualSize != expectedBetweenSize)
          fail(s"For map of dimensions <$w, $h>, " +
              s"expected between size to be <$expectedBetweenSize> but was <$actualSize>. " +
              s"\nBetweens was equal to <${map.betweenObjects.mkString("\n")}>")
      }
    }
    "Set should be correct" in {
      createBattleMap(1, 1).betweenObjects shouldSetEqual
          DirectionalMapPoint.around(MapPoint(0, 0)).strengthR(EmptyBetweenMapObject)
      createBattleMap(2, 2).betweenObjects shouldSetEqual
          Set(
            DirectionalMapPoint(0, 0, Direction.Up),
            DirectionalMapPoint(0, 0, Direction.Right),
            DirectionalMapPoint(0, 0, Direction.Down),
            DirectionalMapPoint(0, 0, Direction.Left),
            DirectionalMapPoint(1, 0, Direction.Down),
            DirectionalMapPoint(1, 0, Direction.Right),
            DirectionalMapPoint(1, 0, Direction.Up),
            DirectionalMapPoint(0, 1, Direction.Down),
            DirectionalMapPoint(0, 1, Direction.Left),
            DirectionalMapPoint(0, 1, Direction.Right),
            DirectionalMapPoint(1, 1, Direction.Down),
            DirectionalMapPoint(1, 1, Direction.Right)
          ).map(e => e -> EmptyBetweenMapObject)
    }
    "directions should be different" in {
      val p = MapPoint(8, 2)
      for (direction <- Direction.values) {
        val $ = createBattleMap(10, 5).place(DirectionalMapPoint(p, direction), Wall)
        $.isOccupiedAt(DirectionalMapPoint(p, direction)) shouldReturn true
        for (d <- Direction.values; if d != direction) {
          $.isOccupiedAt(DirectionalMapPoint(p, d)) shouldReturn false
          $.isOccupiedAt(DirectionalMapPoint(p, d)) shouldReturn false
          $.isOccupiedAt(DirectionalMapPoint(p, d)) shouldReturn false
        }
      }
    }
  }
  "Place" - {
    "Between" in {
      val pd = DirectionalMapPoint(0, 0, Direction.Down)
      createBattleMap(1, 2).place(pd, Wall)(pd) shouldReturn Wall
    }
  }
  "toGraph" in {
    val g = createBattleMap(2, 2).place(DirectionalMapPoint(0, 0, Direction.Right), Wall).toGraph
    g.nodes.map(_.value) shouldSetEqual Seq(
      MapPoint(0, 0),
      MapPoint(1, 0),
      MapPoint(0, 1),
      MapPoint(1, 1),
    )
    g.edges.map(e => UnDiEdge(e._1.value, e._2.value)) shouldSetEqual Seq(
      UnDiEdge(MapPoint(0, 0), MapPoint(0, 1)),
      UnDiEdge(MapPoint(0, 1), MapPoint(1, 1)),
      UnDiEdge(MapPoint(1, 1), MapPoint(1, 0)),
    )
  }

  "betweenPoints" in {
    val $ = createBattleMap(width = 5, height = 10)
    $.betweenPoints.size shouldReturn 5 * 10 * 2 + 5 + 10
    $.betweenPoints.toSet.size shouldReturn 5 * 10 * 2 + 5 + 10
  }

  "wallItUp" in {
    noException shouldBe thrownBy {createBattleMap(2, 1).clearAllPoints.fillItAll.wallItUp}
  }
}


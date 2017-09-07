package com.nessie.model.map

import common.AuxSpecs
import org.scalatest.FreeSpec

import scalax.collection.GraphEdge.UnDiEdge

abstract class BattleMapTest extends FreeSpec with AuxSpecs {
  protected def createBattleMap(width: Int, height: Int): BattleMap

  "Constructor" - {
    "Negative width" in { an[IllegalArgumentException] should be thrownBy createBattleMap(-3, 10) }
    "Zero width" in { an[IllegalArgumentException] should be thrownBy createBattleMap(0, 10) }
    "Negative height" in { an[IllegalArgumentException] should be thrownBy createBattleMap(3, -10) }
    "Zero height" in { an[IllegalArgumentException] should be thrownBy createBattleMap(3, 0) }
    "starts out as all empty" in { DictBattleMap(10, 20).points.map(_._2).forall(_ == EmptyMapObject) shouldReturn true }
  }
  "Attributes" in {
    val $ = createBattleMap(width = 5, height = 10)
    $.width shouldReturn 5
    $.height shouldReturn 10
    $.size shouldReturn 50
  }
  "Exceptions" - {
    val objectPoint = MapPoint(0, 0)
    val emptyPoint = MapPoint(1, 1)
    val betweenPoints = DirectionalMapPoint(objectPoint, Direction.Left)
    val emptyBetween = DirectionalMapPoint(1, 1, Direction.Up)
    val $ = createBattleMap(2, 2)
        .place(objectPoint, NonEmptyBattleMapObject)
        .place(betweenPoints, NonEmptyBetweenMapObject)
    "Out of bounds" - { // Negatives indices handled by the appropriate case class constructor
      "Object" in {
        an[IndexOutOfBoundsException] should be thrownBy $.place(MapPoint(2, 1), NonEmptyBattleMapObject)
        an[IndexOutOfBoundsException] should be thrownBy $.place(MapPoint(1, 2), NonEmptyBattleMapObject)
      }
      "Between" in {
        an[IndexOutOfBoundsException] should be thrownBy $.place(DirectionalMapPoint(2, 1, Direction.Down), NonEmptyBetweenMapObject)
        an[IndexOutOfBoundsException] should be thrownBy $.place(DirectionalMapPoint(1, 2, Direction.Down), NonEmptyBetweenMapObject)
      }
    }
    "Place on non-empty" - {
      "Object" in { an[MapOccupiedException] should be thrownBy $.place(objectPoint, NonEmptyBattleMapObject) }
      "Between" in { an[MapOccupiedException] should be thrownBy $.place(betweenPoints, NonEmptyBetweenMapObject) }
    }
    "Place empty" - {
      "Object" in { an[IllegalArgumentException] should be thrownBy $.place(emptyPoint, EmptyMapObject) }
      "Between" in { an[IllegalArgumentException] should be thrownBy $.place(emptyBetween, EmptyBetweenMapObject) }
    }
    "Remove empty" - {
      "Object" in { an[MapEmptyException] should be thrownBy $.remove(MapPoint(1, 1)) }
      "Between" in { an[MapEmptyException] should be thrownBy $.remove(DirectionalMapPoint(0, 0, Direction.Down)) }
    }
  }
  "Foreach" - {
    "betweens" - {
      "sizes" in {
        for (w <- 1 to 10; h <- 1 to 10) {
          val map = createBattleMap(width = w, height = h)
          val expectedBetweenSize = w * 2 + h * 2 + (w - 1) * h + (h - 1) * w
          val actualSize = map.betweens.map(_._1).toSet.size
          if (actualSize != expectedBetweenSize)
             fail(s"For map of dimensions <$w, $h>, " +
                 s"expected between size to be <$expectedBetweenSize> but was <$actualSize>. " +
                 s"\nBetweens was equal to <${map.betweens.mkString("\n")}>")
        }
      }
      "Set should be correct" in {
        createBattleMap(1, 1).betweens shouldSetEqual
            Direction.values.map(DirectionalMapPoint(0, 0, _)).map(e => e -> EmptyBetweenMapObject)
        createBattleMap(2, 2).betweens shouldSetEqual
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
    }
  }
  "Place" - {
//    "Object" in { ??? }
    "Between" in {
      val pd = DirectionalMapPoint(0, 0, Direction.Down)
      createBattleMap(1, 2).place(pd, Wall)(pd) shouldReturn Wall
    }
  }
  "toGraph" - {
    "With Walls" in {
      val place = createBattleMap(1, 2).place(DirectionalMapPoint(0, 0, Direction.Down), Wall)
      place.toGraph.find(UnDiEdge(MapPoint(0, 0), MapPoint(0, 1))) shouldReturn None
    }
//    "Foo" in {
//      val g = createBattleMap(2, 2).place(DirectionalMapPoint(0, 0, Direction.RIGHT), Wall).toGraph
//      g.nodes.map(_.value) shouldSetEqual List(MapPoint(0, 0), MapPoint(1, 0), MapPoint(0, 1), MapPoint(1, 1))
//      g.edges.map(e => UnDiEdge(e._1.value, e._2.value)) shouldSetEqual List(
//        UnDiEdge(MapPoint(0, 0), MapPoint(0, 1)),
//        UnDiEdge(MapPoint(0, 1), MapPoint(1, 1)),
//        UnDiEdge(MapPoint(1, 1), MapPoint(1, 0))
//      )
//    }
  }
}


package com.nessie.model.map

import common.AuxSpecs
import common.rich.func.MoreIterableInstances
import org.scalatest.FreeSpec
import scalax.collection.GraphEdge.UnDiEdge
import scalaz.syntax.ToFunctorOps

abstract class BattleMapTest extends FreeSpec with AuxSpecs
    with ToFunctorOps with MoreIterableInstances {
  protected def createBattleMap(gs: GridSize): BattleMap
  private def createBattleMap(width: Int, height: Int): BattleMap =
    createBattleMap(GridSize(width = width, height = height))

  "Constructor" - {
    "Negative width" in {an[IllegalArgumentException] should be thrownBy createBattleMap(-3, 10)}
    "Zero width" in {an[IllegalArgumentException] should be thrownBy createBattleMap(0, 10)}
    "Negative height" in {an[IllegalArgumentException] should be thrownBy createBattleMap(3, -10)}
    "Zero height" in {an[IllegalArgumentException] should be thrownBy createBattleMap(3, 0)}
    "starts out as all empty" in {
      createBattleMap(10, 20).objects.map(_._2).forall(_ == EmptyMapObject) shouldReturn true
    }
  }
  "Attributes" in {
    val $ = createBattleMap(width = 5, height = 10)
    $.width shouldReturn 5
    $.height shouldReturn 10
  }
  "Exceptions" - {
    val objectPoint = MapPoint(0, 0)
    val $ = createBattleMap(5, 2).place(objectPoint, NonEmptyBattleMapObject)
    "Out of bounds" - { // Negatives indices handled by the appropriate case class constructor
      "Object" in {
        an[IndexOutOfBoundsException] should be thrownBy $.place(MapPoint(5, 1), NonEmptyBattleMapObject)
        an[IndexOutOfBoundsException] should be thrownBy $.place(MapPoint(1, 2), NonEmptyBattleMapObject)
        an[IndexOutOfBoundsException] should be thrownBy $.place(MapPoint(-1, 1), NonEmptyBattleMapObject)
        an[IndexOutOfBoundsException] should be thrownBy $.place(MapPoint(1, -1), NonEmptyBattleMapObject)
      }
    }
  }

  "toGraph" in {
    val g = createBattleMap(width = 2, height = 3)
        .place(MapPoint(0, 1), FullWall)
        .toObjectGraph
    val node00 = MapPoint(0, 0) -> EmptyMapObject
    val node01 = MapPoint(0, 1) -> FullWall
    val node02 = MapPoint(0, 2) -> EmptyMapObject
    val node10 = MapPoint(1, 0) -> EmptyMapObject
    val node11 = MapPoint(1, 1) -> EmptyMapObject
    val node12 = MapPoint(1, 2) -> EmptyMapObject
    g.nodes.map(_.value) shouldMultiSetEqual Seq(
      node00,
      node01,
      node02,
      node10,
      node11,
      node12,
    )
    g.edges.map(_.toOuter) shouldMultiSetEqual Seq(
      UnDiEdge(node00, node10),
      UnDiEdge(node10, node11),
      UnDiEdge(node11, node12),
      UnDiEdge(node12, node02),
    )
  }

  "toFullGraph" in {
    val g = createBattleMap(width = 2, height = 3)
        .place(MapPoint(0, 1), FullWall)
        .toFullGraph
    val node00 = MapPoint(0, 0)
    val node01 = MapPoint(0, 1)
    val node02 = MapPoint(0, 2)
    val node10 = MapPoint(1, 0)
    val node11 = MapPoint(1, 1)
    val node12 = MapPoint(1, 2)
    g.nodes.map(_.value) shouldMultiSetEqual Seq(
      node00,
      node01,
      node02,
      node10,
      node11,
      node12,
    )
    g.edges.map(_.toOuter) shouldMultiSetEqual Seq(
      UnDiEdge(node00, node01),
      UnDiEdge(node00, node10),
      UnDiEdge(node01, node02),
      UnDiEdge(node01, node11),
      UnDiEdge(node10, node11),
      UnDiEdge(node11, node12),
      UnDiEdge(node12, node02),
    )
  }
}

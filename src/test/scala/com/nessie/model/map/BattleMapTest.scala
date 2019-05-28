package com.nessie.model.map

import common.AuxSpecs
import common.rich.func.MoreIterableInstances
import org.scalatest.FreeSpec
import scalax.collection.GraphEdge.UnDiEdge
import scalaz.syntax.ToFunctorOps

class BattleMapTest extends FreeSpec with AuxSpecs
    with ToFunctorOps with MoreIterableInstances {
  "toGraph" in {
    val g = BattleMap.create(VectorGrid, width = 2, height = 3)
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
}

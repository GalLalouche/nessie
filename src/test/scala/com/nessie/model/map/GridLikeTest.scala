package com.nessie.model.map

import common.AuxSpecs
import common.rich.func.MoreIterableInstances
import monocle.Lens
import org.scalatest.FreeSpec
import org.scalatest.Inspectors._
import scalax.collection.GraphEdge.UnDiEdge
import scalaz.syntax.ToFunctorOps

abstract class GridLikeTest(factory: GridFactory) extends FreeSpec with AuxSpecs
    with ToFunctorOps with MoreIterableInstances {
  private case class GridLikeImpl(grid: Grid[Int]) extends GridLike[GridLikeImpl, Int] {
    override protected def gridLens = Lens[GridLikeImpl, Grid[Int]](_.grid)(grid => _ => GridLikeImpl(grid))
  }
  private def createGrid(width: Int, height: Int): GridLikeImpl =
    GridLikeImpl(factory(GridSize(width, height), 0))

  "Constructor" - {
    "Negative width" in {an[IllegalArgumentException] should be thrownBy createGrid(-3, 10)}
    "Zero width" in {an[IllegalArgumentException] should be thrownBy createGrid(0, 10)}
    "Negative height" in {an[IllegalArgumentException] should be thrownBy createGrid(3, -10)}
    "Zero height" in {an[IllegalArgumentException] should be thrownBy createGrid(3, 0)}
    "starts out as all empty" in {
      forAll(createGrid(10, 20).objects.map(_._2))(_ == 0)
    }
  }
  "Attributes" in {
    val $ = createGrid(width = 5, height = 10)
    $.width shouldReturn 5
    $.height shouldReturn 10
  }
  "Exceptions" - {
    val objectPoint = MapPoint(0, 0)
    val $ = createGrid(5, 2).place(objectPoint, 20)
    "Out of bounds" - { // Negatives indices handled by the appropriate case class constructor
      "Object" in {
        an[IndexOutOfBoundsException] should be thrownBy $.place(MapPoint(5, 1), 1)
        an[IndexOutOfBoundsException] should be thrownBy $.place(MapPoint(1, 2), 1)
        an[IndexOutOfBoundsException] should be thrownBy $.place(MapPoint(-1, 1), 1)
        an[IndexOutOfBoundsException] should be thrownBy $.place(MapPoint(1, -1), 1)
      }
    }
  }

  "toFullGraph" in {
    val g = createGrid(width = 2, height = 3)
        .place(MapPoint(0, 1), 1)
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

  "map" in {
    createGrid(width = 2, height = 3)
        .place(MapPoint(0, 0), 1)
        .place(MapPoint(0, 1), 2)
        .place(MapPoint(0, 2), 3)
        .place(MapPoint(1, 0), 4)
        .place(MapPoint(1, 1), 5)
        .place(MapPoint(1, 2), 6)
        .map(i => i * i) shouldReturn createGrid(width = 2, height = 3)
        .place(MapPoint(0, 0), 1)
        .place(MapPoint(0, 1), 4)
        .place(MapPoint(0, 2), 9)
        .place(MapPoint(1, 0), 16)
        .place(MapPoint(1, 1), 25)
        .place(MapPoint(1, 2), 36)
  }
}

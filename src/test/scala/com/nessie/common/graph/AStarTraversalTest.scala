package com.nessie.common.graph

import com.nessie.common.rng.{RngableDeterministicTest, StdGen}
import com.nessie.model.map.MapPoint
import common.rich.collections.RichSeq._
import common.AuxSpecs
import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import scalax.collection.Graph
import scalax.collection.GraphPredef._

class AStarTraversalTest extends PropSpec with AuxSpecs with GeneratorDrivenPropertyChecks with Matchers {
  property("single line path") {
    AStarTraversal(Graph(1 ~ 2, 2 ~ 3, 3 ~ 4, 4 ~ 5), 1, 5).mkRandom(StdGen(0))
        .toVector shouldReturn 1.to(5).toVector
  }
  property("deterministic") {
    val p0 = MapPoint(0, 0)
    val p1 = MapPoint(0, 1)
    val p2 = MapPoint(1, 0)
    val p3 = MapPoint(1, 1)
    RngableDeterministicTest.forAll {
      // It's important to shuffle the graph's input before starting, otherwise the output be deterministic
      // only because the graph traversal is deterministic (which isn't guaranteed in general for all graphs).
      AStarTraversal(Graph(Vector(p0 ~ p1, p1 ~ p3, p0 ~ p2, p2 ~ p3).shuffle: _*), p0, p3).map(_.toVector)
    }
  }
}

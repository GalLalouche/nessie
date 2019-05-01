package com.nessie.common.graph

import com.nessie.common.rng.StdGen
import common.AuxSpecs
import org.scalatest.FreeSpec
import scalax.collection.Graph
import scalax.collection.GraphPredef._

class DfsTraversalTest extends FreeSpec with AuxSpecs {
  "linear" in {
    val graph = Graph(1 ~ 2, 2 ~ 3, 3 ~ 4, 5 ~ 6) // 5 ~ 6 are ignored
    DfsTraversal(graph, 1).mkRandom(StdGen.fromSeed(0)).toVector shouldReturn Vector(1, 2, 3, 4)
    DfsTraversal(graph, 4).mkRandom(StdGen.fromSeed(0)).toVector shouldReturn Vector(4, 3, 2, 1)
  }
  "take" in {
    val graph = Graph(1 ~ 2, 2 ~ 3, 3 ~ 4, 5 ~ 6) // 5 ~ 6 are ignored
    DfsTraversal(graph, 1).mkRandom(StdGen.fromSeed(0)).take(3).toVector shouldReturn Vector(1, 2, 3)
  }
  "branching" in {
    val $ = DfsTraversal(Graph(1 ~ 2, 2 ~ 3, 3 ~ 4, 1 ~ 5, 1 ~ 6, 7 ~ 8), 2).mkRandom(StdGen.fromSeed(0))
    $ shouldSetEqual 1.to(6)
    $.size shouldReturn 6
    $.head shouldReturn 2
  }
}

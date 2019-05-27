package com.nessie.common.graph

import com.nessie.common.rng.StdGen
import common.AuxSpecs
import org.scalatest.FreeSpec
import scalax.collection.Graph
import scalax.collection.GraphPredef._

class AStarTraversalTest extends FreeSpec with AuxSpecs {
  "dead simple" in {
    AStarTraversal(Graph(1 ~ 2, 2 ~ 3, 3 ~ 4, 4 ~ 5), 1, 5).mkRandom(StdGen(0))
        .toVector shouldReturn 1.to(5).toVector
  }
}

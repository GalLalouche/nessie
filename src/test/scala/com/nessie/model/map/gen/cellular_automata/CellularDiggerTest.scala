package com.nessie.model.map.gen.cellular_automata

import com.nessie.common.graph.RichUndirected._
import com.nessie.common.rng.{Rngable, RngableDeterministicTest, StdGen}
import com.nessie.model.map.{BattleMap, GridSize}
import common.AuxSpecs
import org.scalatest.FreeSpec
import org.scalatest.tags.Slow

@Slow
class CellularDiggerTest extends FreeSpec with AuxSpecs {
  private def generator: Rngable[BattleMap] = {
    val g = CellularAutomataGenerator.generate(GridSize(25, 25))
    for {
      caves <- g.finalStep
      tunnels <- CellularDigger.iterator(caves).finalStep
    } yield g.canonize(tunnels)
  }
  "When finished, all caves are connected" in {
    generator.mkRandom(StdGen(0)).passablePointGraph.stronglyConnectedComponents should have size 1
  }
  "deterministic" in {
    RngableDeterministicTest(generator)
  }
}

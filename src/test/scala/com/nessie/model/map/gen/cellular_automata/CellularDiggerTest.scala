package com.nessie.model.map.gen.cellular_automata

import com.nessie.common.graph.RichUndirected._
import com.nessie.common.rng.{Rngable, RngableDeterministicTest, StdGen}
import com.nessie.model.map.BattleMap
import common.AuxSpecs
import org.scalatest.FreeSpec
import org.scalatest.tags.Slow

@Slow
class CellularDiggerTest extends FreeSpec with AuxSpecs {
  private def generator: Rngable[BattleMap] = for {
    caves <- CellularAutomataGenerator.generator
    tunnels <- CellularDigger.generator(caves).generator
  } yield CellularAutomataGenerator.canonize(tunnels)
  "When finished, all caves are connected" in {
    generator.mkRandom(StdGen(0)).passablePointGraph.stronglyConnectedComponents should have size 1
  }
  "deterministic" in {
    RngableDeterministicTest(generator)
  }
}

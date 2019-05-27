package com.nessie.model.map.gen.cellular_automata

import com.nessie.common.rng.RngableDeterministicTest
import common.AuxSpecs
import org.scalatest.FreeSpec

class CellularAutomataGeneratorTest extends FreeSpec with AuxSpecs {
  "deterministic" in {
    RngableDeterministicTest {
      CellularAutomataGenerator.generator.map(CellularAutomataGenerator.canonize)
    }
  }
}

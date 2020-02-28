package com.nessie.model.map.gen.cellular_automata

import com.nessie.common.rng.RngableDeterministicTest
import com.nessie.model.map.GridSize
import common.test.AuxSpecs
import org.scalatest.FreeSpec

class CellularAutomataGeneratorTest extends FreeSpec with AuxSpecs {
  "deterministic" in {
    RngableDeterministicTest {
      val generator = CellularAutomataGenerator.generate(GridSize(25, 25))
      generator.finalStep.map(generator.canonize)
    }
  }
}

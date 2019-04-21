package com.nessie.common.rng

import common.AuxSpecs
import org.scalatest.PropSpec
import org.scalatest.prop.GeneratorDrivenPropertyChecks

class StdGenTest extends PropSpec with AuxSpecs with GeneratorDrivenPropertyChecks {
  property("Same next every time") {
    forAll {rng: StdGen =>
      rng.next._1 shouldReturn rng.next._1
    }
  }
  property("Different items in next") {
    forAll {rng: StdGen =>
      rng.next._2.next._1 should not equal rng.next._1
    }
  }
  property("Same next-next every time") {
    forAll {rng: StdGen =>
      rng.next._2.next._1 shouldReturn rng.next._2.next._1
    }
  }
  property("Same split every time") {
    forAll {rng: StdGen =>
      rng.split._1.next._1 shouldReturn rng.split._1.next._1
      rng.split._2.next._1 shouldReturn rng.split._2.next._1
    }
  }
  property("Different items in split") {
    forAll {rng: StdGen =>
      rng.split._1.next._1 should not equal rng.split._2.next._1
    }
  }
}

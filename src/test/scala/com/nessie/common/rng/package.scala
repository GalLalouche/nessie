package com.nessie.common

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen

package object rng {
  private[rng] implicit lazy val genStdGen: Gen[StdGen] = arbitrary[Long].map(StdGen.fromSeed)
}

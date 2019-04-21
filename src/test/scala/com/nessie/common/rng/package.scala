package com.nessie.common

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen

package object rng {
  implicit lazy val arbSong: Gen[StdGen] = arbitrary[Long].map(StdGen.fromSeed)
}

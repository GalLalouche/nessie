package com.nessie.common.rng

import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.Matchers

object RngableDeterministicTest extends GeneratorDrivenPropertyChecks with Matchers {
  private implicit def genToArbitrary[A: Gen]: Arbitrary[A] = Arbitrary(implicitly[Gen[A]])
  /** Verifies that the result is deterministic, regardless of seed used. */
  def forAll(rngable: => Rngable[_]): Unit =
    forAll {rng: StdGen => rngable.mkRandom(rng) should ===(rngable.mkRandom(rng))}
  /** Verifies that the result is deterministic for a pre-determined (but random!!!1) seed. */
  def apply(rngable: => Rngable[_]): Unit = {
    val seed = 4 // chosen by fair dice roll.
    // guaranteed to be random.
    rngable.mkRandom(StdGen(seed)) should ===(rngable.mkRandom(StdGen(seed)))
  }
}

package com.nessie.common.rng

import com.nessie.common.rng.Rngable.ToRngableOps
import common.AuxSpecs
import org.scalatest.PropSpec
import org.scalatest.prop.GeneratorDrivenPropertyChecks

class RngableTest extends PropSpec with AuxSpecs with GeneratorDrivenPropertyChecks with ToRngableOps {
  private case class Person(age: Int, name: String)
  private implicit val RngEv: Rngable[Person] = for {
    age <- mkRandom[Int]
    nameLength <- mkRandom[Int].map(_ % 100 + 1)
    name <- Rngable.stringAtLength(nameLength)
  } yield Person(age, name)
  property("Same result every time") {
    forAll((rng: StdGen) => {
      val p1 = mkRandom[Person](rng)
      val p2 = mkRandom[Person](rng)
      p1 shouldReturn p2
    })
  }
  private implicit val RngEv2: Rngable[(Int, Int)] = for {
    x <- mkRandom[Int]
    y <- mkRandom[Int]
  } yield x -> y
  property("Different for loop") {
    forAll((rng: StdGen) => {
      val (x, y) = mkRandom[(Int, Int)](rng)
      x should not equal y
    })
  }

  property("Computation is trampolined") {
    def randomVector(n: Int): Rngable[Vector[Int]] =
      if (n == 0) Rngable.pure[Vector[Int]](Vector.empty) else for {
        x <- mkRandom[Int]
        y <- mkRandom[Int]
        result <- randomVector(n - 2).map(Vector(x, y) ++ _)
      } yield result
    noException shouldBe thrownBy {
      randomVector(100000).mkRandom(StdGen.fromSeed(0))
    }
  }
}

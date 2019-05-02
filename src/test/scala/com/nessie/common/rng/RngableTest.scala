package com.nessie.common.rng

import com.nessie.common.rng.Rngable.ToRngableOps
import common.AuxSpecs
import org.scalatest.PropSpec
import org.scalatest.concurrent.{Signaler, TimeLimitedTests}
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.time.SpanSugar._

import scalaz.std.VectorInstances
import scalaz.syntax.ToTraverseOps

class RngableTest extends PropSpec with AuxSpecs with GeneratorDrivenPropertyChecks with ToRngableOps
    with ToTraverseOps with VectorInstances with TimeLimitedTests {
  override val timeLimit = 1000 millis
  override val defaultTestSignaler = Signaler(_.stop())
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

  property("SequenceU is trampolined") {
    noException shouldBe thrownBy {
      Vector.fill(100000)(Rngable.DoubleEv).sequenceU.mkRandom(StdGen.fromSeed(0))
    }
  }

  property("Rngable.iterate") {
    // TODO ToMoreFunctorOps.when?
    val $ = Rngable.iterate(1)(e => Rngable.BooleanEv.map(b => e + (if (b) 1 else 0)))
        .map(_ take 10)
        .mkRandom(StdGen fromSeed 0)
    $.toVector shouldReturn Vector(1, 2, 2, 3, 3, 4, 4, 4, 5, 6)
    $.toVector shouldReturn Vector(1, 2, 2, 3, 3, 4, 4, 4, 5, 6)
  }

  property("Rngable.iterateOptionally") {
    val $ = Rngable.iterateOptionally(1)(
      e => if (e >= 10) Rngable.pure(None) else Rngable.BooleanEv.map(b => Some(e + (if (b) 1 else 0)))
    ).mkRandom(StdGen fromSeed 0)
    $.toVector shouldReturn Vector(1, 2, 2, 3, 3, 4, 4, 4, 5, 6, 6, 7, 7, 7, 7, 8, 9, 10)
    $.toVector shouldReturn Vector(1, 2, 2, 3, 3, 4, 4, 4, 5, 6, 6, 7, 7, 7, 7, 8, 9, 10)
  }
  property("Rngable.iterateOptionally on infinite") {
    val $ = Rngable.iterateOptionally(1)(e => Rngable.BooleanEv.map(b => Some(e + (if (b) 1 else 0))))
        .map(_.take(20))
        .mkRandom(StdGen fromSeed 0)
    $.toVector shouldReturn Vector(1, 2, 2, 3, 3, 4, 4, 4, 5, 6, 6, 7, 7, 7, 7, 8, 9, 10, 11, 12)
    $.toVector shouldReturn Vector(1, 2, 2, 3, 3, 4, 4, 4, 5, 6, 6, 7, 7, 7, 7, 8, 9, 10, 11, 12)
  }
}

package com.nessie.common.rng

import com.nessie.common.rng.Rngable.ToRngableOps
import org.scalatest.PropSpec
import org.scalatest.concurrent.{Signaler, TimeLimitedTests}
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.time.SpanSugar._

import scala.language.postfixOps

import scalaz.std.VectorInstances
import scalaz.syntax.ToTraverseOps
import common.rich.func.ToMoreFunctorOps

import common.test.AuxSpecs

class RngableTest extends PropSpec with AuxSpecs with GeneratorDrivenPropertyChecks with ToRngableOps
    with ToTraverseOps with VectorInstances with TimeLimitedTests with ToMoreFunctorOps {
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

  property("Seed isn't leaked between random calls") {
    val rng = StdGen(0)
    def seqFromStdGen(stdGen: StdGen) = {
      val random = stdGen.random
      stdGen.seed :: List.fill(10)(random.nextLong())
    }
    val (x, y) = (for {
      x <- Rngable.fromStdGen(seqFromStdGen)
      y <- Rngable.fromStdGen(seqFromStdGen)
    } yield (x, y)).mkRandom(rng)
    x.intersect(y) shouldBe 'empty
  }

  property("From random modifies the stdgen") {
    forAll((rng: StdGen) => {
      val (x, y) = (for {
        x <- Rngable.fromRandom(_.nextInt())
        y <- Rngable.fromRandom(_.nextInt())
      } yield x -> y).mkRandom(rng)
      x should not equal y
    })
  }

  private def verifyIncreasing(xs: Seq[Int]) = xs.sliding(2).foreach(v => v(0) should be <= v(1))
  private def addBinaryRngable(e: Int): Rngable[Int] = Rngable.BooleanEv.when(1, 0).map(e + _)
  property("Rngable.iterate") {
    forAll((rng: StdGen) => {
      val $ = Rngable.iterate(1)(addBinaryRngable)
          .map(_ take 10)
          .mkRandom(rng)
      val vector = $.toVector
      vector.size shouldReturn 10
      verifyIncreasing(vector)
      vector.last should be <= 10
      vector shouldReturn $.toVector
    })
  }

  private def someAddBinaryRngable(e: Int): Rngable[Option[Int]] = addBinaryRngable(e).map(Some.apply)
  property("Rngable.iterateOptionally") {
    forAll((rng: StdGen) => {
      val $ = Rngable.iterateOptionally(1)(
        e => if (e >= 10) Rngable.pure(None) else someAddBinaryRngable(e)
      ).mkRandom(rng)
      val vector = $.toVector
      verifyIncreasing(vector)
      vector.last shouldReturn 10
      vector(vector.length - 2) shouldReturn 9
      vector shouldReturn $.toVector
    })
  }
  property("Rngable.iterateOptionally on infinite") {
    forAll((rng: StdGen) => {
      val $ = Rngable.iterateOptionally(1)(someAddBinaryRngable)
          .map(_.take(20))
          .mkRandom(rng)
      val vector = $.toVector
      verifyIncreasing(vector)
      vector.size shouldReturn 20
      vector shouldReturn $.toVector
    })
  }
}

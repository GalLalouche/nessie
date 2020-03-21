package com.nessie.common.rng

import com.nessie.common.rng.Rngable.ToRngableOps._
import org.scalatest.PropSpec
import org.scalatest.concurrent.{Signaler, TimeLimitedTests}
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.time.SpanSugar._

import scala.language.postfixOps

import scalaz.std.vector.vectorInstance
import scalaz.syntax.monad.ToMonadOps
import scalaz.syntax.traverse.ToTraverseOps
import scalaz.OptionT
import common.rich.func.ToMoreFunctorOps._

import common.test.AuxSpecs

class RngableTest extends PropSpec with AuxSpecs with GeneratorDrivenPropertyChecks with TimeLimitedTests {
  override val timeLimit = 2000 millis
  override val defaultTestSignaler = Signaler(_.stop())
  private case class Person(age: Int, name: String)
  private implicit val RngEv: Rngable[Person] = for {
    age <- mkRandom[Int]
    nameLength <- mkRandom[Int].map(_ % 100 + 1)
    name <- Rngable.stringOfLength(nameLength)
  } yield Person(age, name)
  property("Same result every time") {
    forAll {rng: StdGen =>
      val p1 = mkRandom[Person](rng)
      val p2 = mkRandom[Person](rng)
      p1 shouldReturn p2
    }
  }
  property("Different for loop") {
    implicit val RngEv2: Rngable[(Int, Int)] = for {
      x <- mkRandom[Int]
      y <- mkRandom[Int]
    } yield x -> y
    forAll {rng: StdGen =>
      val (x, y) = mkRandom[(Int, Int)](rng)
      x should not equal y
    }
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
    forAll {rng: StdGen =>
      val (x, y) = (for {
        x <- Rngable.fromRandom(_.nextInt())
        y <- Rngable.fromRandom(_.nextInt())
      } yield x -> y).mkRandom(rng)
      x should not equal y
    }
  }

  private def verifyIncreasing(xs: Seq[Int]) = xs.sliding(2).foreach(v => v(0) should be <= v(1))
  private def addBinaryRngable(e: Int): Rngable[Int] = Rngable.BooleanEv.when(1, 0).map(e + _)
  property("Rngable.iterate") {
    forAll {rng: StdGen =>
      val $ = Rngable.iterate(20)(addBinaryRngable)
          .take(10)
          .mkRandom(rng)
      val vector = $.toVector
      vector.size shouldReturn 10
      verifyIncreasing(vector)
      vector.last should be <= 30
      vector shouldReturn $.toVector
    }
  }
  property("Rngable.iterate deterministic") {
    val $ = Rngable.iterate(1)(Rngable pure _ + 1)
        .take(10)
        .mkRandom(StdGen(0))
    val vector = $.toVector
    vector shouldReturn 1.to(10).toVector
    vector shouldReturn $.toVector
  }

  private def someAddBinaryRngable(e: Int): Rngable[Int] = addBinaryRngable(e)
  property("Rngable.iterateOptionally") {
    forAll {rng: StdGen =>
      val $ = Rngable.iterateOptionally(1)(
        e => Rngable.when(e < 10)(someAddBinaryRngable(e))
      ).mkRandom(rng)
      val vector = $.toVector
      verifyIncreasing(vector)
      vector.last shouldReturn 10
      vector(vector.length - 2) shouldReturn 9
      vector shouldReturn $.toVector
    }
  }
  property("Rngable.iterateOptionally on infinite") {
    forAll {rng: StdGen =>
      val $ = Rngable.iterateOptionally(1)(someAddBinaryRngable(_).liftM[OptionT])
          .take(10)
          .mkRandom(rng)
      val vector = $.toVector
      verifyIncreasing(vector)
      vector.size shouldReturn 10
      vector shouldReturn $.toVector
    }
  }

  property("Rngable.tryNTimes returns None on failure") {
    forAll {rng: StdGen =>
      val $ = Rngable.tryNTimes(10)(Rngable.none).run.mkRandom(rng)
      $ shouldReturn None
    }
  }

  property("Rngable.tryNTimes tries n times") {
    forAll {rng: StdGen =>
      var i = 1
      val $ = Rngable.tryNTimes(10) {
        i += 1
        Rngable.when(i > 10)(Rngable.pure("foobar"))
      }.run.mkRandom(rng)
      $ shouldReturn Some("foobar")
    }
  }

  property("Rngable.tryNTimes tries no more than n times") {
    forAll {rng: StdGen =>
      var i = 0
      val $ = Rngable.tryNTimes(10) {
        i += 1
        Rngable.when(i > 10)(Rngable.pure("foobar"))
      }.run.mkRandom(rng)
      $ shouldReturn None
    }
  }

  property("Rngable.tryNTimes if as first you do succeed, try not to look so surprised") {
    forAll {rng: StdGen =>
      var i = 0
      val $ = Rngable.tryNTimes(10) {
        i += 1
        Rngable.some("foobar")
      }.run.mkRandom(rng)
      $ shouldReturn Some("foobar")
      i shouldReturn 1
    }
  }
}

package com.nessie.view.sfx

import common.AuxSpecs
import org.scalatest.FreeSpec
import org.scalatest.concurrent.{Signaler, TimeLimitedTests}
import org.scalatest.time.SpanSugar._
import scalaz.concurrent.Task

import scala.language.postfixOps

class PromiseZTest extends FreeSpec with AuxSpecs with TimeLimitedTests {
  override val timeLimit = 500 millis
  override val defaultTestSignaler: Signaler = Signaler(_.interrupt())

  "toTask" - {
    "fulfill" - {
      "pre-filled" in {
        val $ = PromiseZ[Int]()
        val t = $.toTask
        $.fulfill(42)
        t.unsafePerformSyncAttemptFor(200).toOption.get shouldReturn 42
      }
      "post-filled" in {
        val $ = PromiseZ[Int]()
        val t = $.toTask
        Task.delay {
          Thread.sleep(50)
          $.fulfill(42)
        }.unsafePerformAsync(_ => ())
        t.unsafePerformSyncAttemptFor(200).toOption.get shouldReturn 42
      }
      "multiple tasks" in {
        val $ = PromiseZ[Int]()
        val t = $.toTask
        $.fulfill(42)
        t.unsafePerformSyncAttemptFor(200).toOption.get shouldReturn 42
        $.toTask.unsafePerformSyncAttemptFor(200).toOption.get shouldReturn 42
        $.toTask.unsafePerformSyncAttemptFor(200).toOption.get shouldReturn 42
      }
    }
    "fail" - {
      val e = new Exception("blah blah")
      "pre-failed" in {
        val $ = PromiseZ[Int]()
        val t = $.toTask
        $.fail(e)
        t.unsafePerformSyncAttemptFor(200).toThese.a.get shouldReturn e
      }
      "post-filled" in {
        val $ = PromiseZ[Int]()
        val t = $.toTask
        Task.delay {
          Thread.sleep(50)
          $.fail(e)
        }.unsafePerformAsync(_ => ())
        t.unsafePerformSyncAttemptFor(200).toThese.a.get shouldReturn e
      }
      "multiple tasks" in {
        val $ = PromiseZ[Int]()
        val t = $.toTask
        $.fail(e)
        t.unsafePerformSyncAttemptFor(200).toThese.a.get shouldReturn e
        $.toTask.unsafePerformSyncAttemptFor(200).toThese.a.get shouldReturn e
        $.toTask.unsafePerformSyncAttemptFor(200).toThese.a.get shouldReturn e
      }
    }
  }

  "multiple actions throw an exception" - {
    "fulfill then fail" in {
      val $ = PromiseZ[Int]()
      $.fulfill(42)
      an[IllegalStateException] shouldBe thrownBy {$.fail(new Exception())}
    }
    "fulfill then fulfill" in {
      val $ = PromiseZ[Int]()
      $.fulfill(42)
      an[IllegalStateException] shouldBe thrownBy {$.fulfill(43)}
    }
    "fail then fail" in {
      val $ = PromiseZ[Int]()
      $.fail(new Exception())
      an[IllegalStateException] shouldBe thrownBy {$.fail(new Exception())}
    }
    "fail then fulfill" in {
      val $ = PromiseZ[Int]()
      $.fail(new Exception())
      an[IllegalStateException] shouldBe thrownBy {$.fulfill(43)}
    }
  }
}

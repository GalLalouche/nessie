package com.nessie.view.sfx

import common.AuxSpecs
import org.scalatest.{FreeSpec, OneInstancePerTest}
import org.scalatest.concurrent.{Signaler, TimeLimitedTests}
import org.scalatest.time.SpanSugar._

import scalaz.concurrent.Task
import scala.language.postfixOps

class PromiseZTest extends FreeSpec with AuxSpecs with TimeLimitedTests with OneInstancePerTest {
  override val timeLimit = 500 millis
  override val defaultTestSignaler: Signaler = Signaler(_.interrupt())

  private def checkReturn[A](task: Task[A], e: A): Unit =
    task.unsafePerformSyncAttemptFor(200).toOption.get shouldReturn e
  private def checkFailure(task: Task[_], e: Exception): Unit =
    task.unsafePerformSyncAttemptFor(200).toThese.a.get shouldReturn e
  "toTask" - {
    val $ = PromiseZ[Int]()
    "fulfill" - {
      "pre-filled" in {
        $.fulfill(42)
        checkReturn($.toTask, 42)
      }
      "post-filled" in {
        val task = $.toTask
        Task.delay {
          Thread.sleep(50)
          $.fulfill(42)
        }.unsafePerformAsync(_ => ())
        checkReturn(task, 42)
      }
      "multiple tasks" in {
        val task = $.toTask
        $.fulfill(42)
        checkReturn(task, 42)
        checkReturn($.toTask, 42)
        checkReturn($.toTask, 42)
      }
    }
    "fail" - {
      val e = new Exception("blah blah")
      "pre-failed" in {
        $.fail(e)
        checkFailure($.toTask, e)
      }
      "post-filled" in {
        val task = $.toTask
        Task.delay {
          Thread.sleep(50)
          $.fail(e)
        }.unsafePerformAsync(_ => ())
        checkFailure(task, e)
      }
      "multiple tasks" in {
        val task = $.toTask
        $.fail(e)
        checkFailure(task, e)
        checkFailure($.toTask, e)
        checkFailure($.toTask, e)
      }
    }
  }

  "multiple actions throw an exception" - {
    val $ = PromiseZ[Int]()
    "fulfill then fail" in {
      $.fulfill(42)
      an[IllegalStateException] shouldBe thrownBy {$.fail(new Exception())}
    }
    "fulfill then fulfill" in {
      $.fulfill(42)
      an[IllegalStateException] shouldBe thrownBy {$.fulfill(43)}
    }
    "fail then fail" in {
      $.fail(new Exception())
      an[IllegalStateException] shouldBe thrownBy {$.fail(new Exception())}
    }
    "fail then fulfill" in {
      $.fail(new Exception())
      an[IllegalStateException] shouldBe thrownBy {$.fulfill(43)}
    }
  }

  "map" - {
    val $ = PromiseZ[String]()
    def intTask = $.map(_.length).toTask

    "fulfill source" - {
      "pre-filled" in {
        $.fulfill("foobar")
        checkReturn($.toTask, "foobar")
        checkReturn(intTask, 6)
      }
      "post-filled" in {
        val task = $.toTask
        val task2 = intTask
        Task.delay {
          Thread.sleep(50)
          $.fulfill("foobar")
        }.unsafePerformAsync(_ => ())
        checkReturn(task, "foobar")
        checkReturn(task2, 6)
      }
      "multiple tasks" in {
        $.fulfill("foobar")
        checkReturn($.toTask, "foobar")
        checkReturn(intTask, 6)
      }
    }
    "fail source" - {
      val e = new Exception("blah blah")
      "pre-failed" in {
        $.fail(e)
        checkFailure($.toTask, e)
        checkFailure(intTask, e)
      }
      "post-filled" in {
        val task = $.toTask
        val task2 = intTask
        Task.delay {
          Thread.sleep(50)
          $.fail(e)
        }.unsafePerformAsync(_ => ())
        checkFailure(task, e)
        checkFailure(task2, e)
      }
      "multiple tasks" in {
        val task = $.toTask
        val task2 = intTask
        $.fail(e)
        checkFailure(task, e)
        checkFailure(task2, e)
      }
    }
  }
}

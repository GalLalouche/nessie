package com.nessie.view.sfx

import common.rich.RichT._
import rx.lang.scala.subjects.ReplaySubject
import scalaz.concurrent.Task
import scalaz.{-\/, \/, \/-}

/** Because god forbid ScalaZ have a proper Promise. */
// TODO move to common
private class PromiseZ[A] {
  private type Value = Throwable \/ A
  private val ps = ReplaySubject[Value]()
  private var value: Value = _
  def fulfill(a: A): Unit = this.synchronized {
    if (value != null)
      throw new IllegalStateException("Promise already filled")
    value = \/-(a)
    ps.onNext(value)
  }
  def fail(throwable: Throwable): Unit = this.synchronized {
    if (value != null)
      throw new IllegalStateException("Promise already filled")
    value = -\/(throwable)
    ps.onNext(value)
  }
  def toTask: Task[A] = this.synchronized {
    value.opt match {
      case None => Task.async[A](ps.subscribe(_))
      case Some(-\/(e)) => Task.fail(e)
      case Some(\/-(v)) => Task.now(v)
    }
  }
}

private object PromiseZ {
  def apply[A](): PromiseZ[A] = new PromiseZ[A]()
}

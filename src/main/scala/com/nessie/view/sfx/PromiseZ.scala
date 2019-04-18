package com.nessie.view.sfx

import com.nessie.view.sfx.PromiseZ._
import common.rich.RichT._
import rx.lang.scala.subjects.ReplaySubject
import rx.lang.scala.Subscriber

import scalaz.{-\/, \/, \/-}
import scalaz.concurrent.Task

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
      case None => Task.async(callback => ps.subscribe(singleTimeSubscriber(callback)))
      case Some(-\/(e)) => Task.fail(e)
      case Some(\/-(v)) => Task.now(v)
    }
  }
}

private object PromiseZ {
  // TODO move to ScalaCommon
  private def singleTimeSubscriber[A](f: A => Any): Subscriber[A] = new Subscriber[A]() {
    override def onNext(value: A): Unit = {
      f(value)
      unsubscribe()
    }
  }
  def apply[A](): PromiseZ[A] = new PromiseZ[A]()
}

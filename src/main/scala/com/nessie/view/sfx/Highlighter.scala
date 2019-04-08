package com.nessie.view.sfx

import rx.lang.scala.Observer

import scalafx.scene.input.MouseEvent

//TODO overly general?
private trait Highlighter[T] {
  def highlight(t: T): Unit
  def disableHighlighting(t: T): Unit
  def observer: Observer[(MouseEvent, T)] = new Observer[(MouseEvent, T)] {
    override def onNext(value: (MouseEvent, T)) = value._1.eventType match {
      case MouseEvent.MouseEntered => highlight(value._2)
      case MouseEvent.MouseExited => disableHighlighting(value._2)
      case _ => ()
    }
    override def onError(error: Throwable) = ???
    override def onCompleted() = ???
  }
}

private object Highlighter {
  def composite[T](hs: Highlighter[T]*) = new Highlighter[T] {
    override def highlight(t: T) = hs.foreach(_ highlight t)
    override def disableHighlighting(t: T) = hs.foreach(_ disableHighlighting t)
  }
}

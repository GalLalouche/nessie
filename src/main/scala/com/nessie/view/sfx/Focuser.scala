package com.nessie.view.sfx

import rx.lang.scala.Observer
import scalafx.scene.input.MouseEvent

/**
 * Focuses the element in the UI when the responding element is hovered on in other parts of the UI. For
 * example, when hovering over a unit in the grid, highlight it in the event queue display.
 */
private trait Focuser[A] {
  def focus(t: A): Unit
  def unfocus(t: A): Unit
}

private object Focuser {
  def observer[A](h: Focuser[A]) = new Observer[(MouseEvent, A)] {
    override def onNext(value: (MouseEvent, A)) = value._1.eventType match {
      case MouseEvent.MouseEntered => h.focus(value._2)
      case MouseEvent.MouseExited => h.unfocus(value._2)
      case _ => ()
    }
    override def onError(error: Throwable) = ???
    override def onCompleted() = ()
  }
  def composite[T](hs: Focuser[T]*) = new Focuser[T] {
    override def focus(t: T) = hs.foreach(_ focus t)
    override def unfocus(t: T) = hs.foreach(_ unfocus t)
  }
}

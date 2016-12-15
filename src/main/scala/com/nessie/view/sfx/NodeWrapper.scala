package com.nessie.view.sfx

import javafx.event.{Event, EventHandler, EventType}
import javafx.scene.{control => jfxsc}

import rx.lang.scala.Observable
import rx.lang.scala.subjects.PublishSubject

import scalafx.scene.Node
import scalafx.scene.control.{Button, Label}

private trait NodeWrapper {
  protected implicit def jfx2sfx(node: javafx.scene.Node): scalafx.scene.Node = node match {
    case b: jfxsc.Button => new Button(b)
    case l: jfxsc.Label => new Label(l)
    case _ => throw new Exception("Unsupported conversion for " + node.getClass)
  }
  protected def toObserver[T <: Event](eventType: EventType[T], node: Node): Observable[T] = {
    val $ = PublishSubject[T]()
    node.addEventHandler[T](eventType, new EventHandler[T] {
      override def handle(event: T): Unit = $ onNext event
    })
    $
  }
  def node: Node
}

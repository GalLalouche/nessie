package com.nessie.view.sfx

import javafx.event._
import javafx.scene.{control => jfxsc, layout => jfxl}
import com.nessie.model.units.CombatUnit
import common.rich.RichT._
import common.rich.func.MoreObservableInstances
import rx.lang.scala.Observable
import rx.lang.scala.subjects.PublishSubject
import scalafx.application.Platform
import scalafx.scene.Node
import scalafx.scene.control.{Button, Label}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.BorderPane

import scalaz.syntax.ToFunctorOps

private trait NodeWrapper {
  def node: Node
}

private object NodeWrapper
    extends ToFunctorOps with MoreObservableInstances {
  def jfx2sfx(node: javafx.scene.Node): scalafx.scene.Node = node match {
    case b: jfxsc.Button => new Button(b)
    case l: jfxsc.Label => new Label(l)
    case b: jfxl.BorderPane => new BorderPane(b)
    case _ => throw new Exception("Unsupported conversion for " + node.getClass)
  }
  def toObservable[T <: Event](eventType: EventType[T], node: Node): Observable[T] = {
    val $ = PublishSubject[T]()
    node.addEventHandler[T](eventType, $.onNext)
    $
  }
  def mouseEvents(n: Node): Observable[MouseEvent] = toObservable(MouseEvent.Any, n).map(new MouseEvent(_))
  def mouseEvents[Key](m: Traversable[(Key, Node)]): Observable[(MouseEvent, Key)] = m.map {
    case (key, node) => mouseEvents(node).strengthR(key)
  }.reduce(_ merge _)
  def shortName(u: CombatUnit): String = u.simpleName.take(2)

  def setBackgroundColor(color: String)(n: Node): Unit =
    Platform.runLater(n.setStyle(Styles.backgroundColor(color)))
  def setBaseColor(color: String)(n: Node): Unit = Platform.runLater(n.setStyle(Styles.baseColor(color)))
  def setFontWeight(style: String)(n: Node): Unit = Platform.runLater(n.setStyle(Styles.fontWeight(style)))
}

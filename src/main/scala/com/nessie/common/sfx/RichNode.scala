package com.nessie.common.sfx

import javafx.event.{Event, EventType}
import rx.lang.scala.Observable
import scalafx.application.Platform.runLater
import scalafx.scene.input.MouseEvent
import scalafx.scene.{Node => SNode}

import common.rich.RichObservable

object RichNode {
  implicit class richNode[N](node: N)(implicit ev: NodeLike[N]) {
    private val n = ev.scalaNode(node)
    def setBackgroundColor(color: String): Unit = runLater(n.setStyle(Styles.backgroundColor(color)))
    def setBaseColor(color: String): Unit = runLater(n.setStyle(Styles.baseColor(color)))
    def setFontWeight(style: String): Unit = runLater(n.setStyle(Styles.fontWeight(style)))
    def setFontSize(size: Int): Unit = runLater(n.setStyle(Styles.fontSize(size)))

    def toObservable[T <: Event](eventType: EventType[T]): Observable[T] =
      RichObservable.register(f => n.addEventHandler[T](eventType, f.apply))
    def mouseEvents: Observable[MouseEvent] = toObservable(MouseEvent.Any).map(new MouseEvent(_))

    def toScalaNode: SNode = n
  }
}

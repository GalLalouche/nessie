package com.nessie.view.sfx

import javafx.event.{Event, EventType}
import rx.lang.scala.Observable
import rx.lang.scala.subjects.PublishSubject
import scalafx.application.Platform.runLater
import scalafx.scene.{Node => SNode}
import scalafx.scene.input.MouseEvent

private object RichNode {
  implicit class richNode[N](node: N)(implicit ev: NodeLike[N]) {
    private val n = ev.scalaNode(node)
    def setBackgroundColor(color: String): Unit = runLater(n.setStyle(Styles.backgroundColor(color)))
    def setBaseColor(color: String): Unit = runLater(n.setStyle(Styles.baseColor(color)))
    def setFontWeight(style: String): Unit = runLater(n.setStyle(Styles.fontWeight(style)))

    def toObservable[T <: Event](eventType: EventType[T]): Observable[T] = {
      val $ = PublishSubject[T]()
      n.addEventHandler[T](eventType, $.onNext)
      $
    }
    def mouseEvents: Observable[MouseEvent] = toObservable(MouseEvent.Any).map(new MouseEvent(_))

    def toScalaNode: SNode = n
  }
}

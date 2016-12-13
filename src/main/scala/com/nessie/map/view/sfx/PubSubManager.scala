package com.nessie.map.view.sfx

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scalafx.scene.input.MouseEvent

private class PubSubManager[M] {
  private val subscribers = new java.util.LinkedList[M => Any]
  def unsubscribe(h: M => Any): Boolean = {
    subscribers.remove(h)
  }
  def publish(m: M): Unit = {
    val i = subscribers.iterator
    while (i.hasNext) {
      val $ = i.next()(m)
      $ match {
        case false => i.remove()
        case _ => ()
      }
    }
  }
  def subscribe(h: M => Any): Unit = {
    subscribers.add(h)
  }
}

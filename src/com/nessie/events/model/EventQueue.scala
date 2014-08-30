package com.nessie.events.model

import common.rich.collections.RichSeq._

class EventQueue private(q: List[(Event, Double)]) extends Iterable[Event] {
  private implicit def qToEQ(eq: Seq[(Event, Double)]) = new EventQueue(eq.toList)

  // this ensures by induction that there are no null events
  require(q.isEmpty || q.head != null)

  def this() = this(List[(Event, Double)]())
  override def iterator = q.iterator.map(_._1)

  def +(e: Event): EventQueue = add(e, 0.0)
  def add(e: Event, withDelay: Double): EventQueue = add((e, withDelay))
  private def add(eventWithDelay: (Event, Double)): EventQueue = q
    .findIndex(_._2 > eventWithDelay._2)
    .map(i => q insert eventWithDelay at i)
    .getOrElse((eventWithDelay :: q.reverse).reverse)
    .toList
  def next: EventQueue = new EventQueue(q.tail)
}
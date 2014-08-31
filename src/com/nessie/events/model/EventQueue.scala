package com.nessie.events.model

import common.rich.collections.RichSeq._

class EventQueue[T] private(q: List[(T, Double)], currentDelay: Double) extends Iterable[T] {
  private implicit def qToEQ(eq: (List[(T, Double)], Double)) = new EventQueue[T](eq._1, eq._2)

  // this ensures by induction that there are no null events
  require(q.isEmpty || q.head._1 != null)

  def this() = this(List[(T, Double)](), 0.0)
  override def iterator = q.iterator.map(_._1)

  def +(e: T): EventQueue[T] = add(e, 0.0)
  def add(e: T, withDelay: Double): EventQueue[T] = add((e, withDelay + currentDelay))
  private def add(eventWithDelay: (T, Double)): EventQueue[T] =
    (q
      .findIndex(_._2 > eventWithDelay._2)
      .map(i => q insert eventWithDelay at i)
      .getOrElse((eventWithDelay :: q.reverse).reverse)
      .toList, currentDelay)
  def next: EventQueue[T] = (q.tail, q.head._2 + currentDelay)
  def repeat(e: T) = new {
    def times(n: Int) = new {
      require(n > 0, "The number of repeats must be a positive integer")
      def inIntervalsOf(delay: Double) = new {
        require(delay >= 0, "The intervals length must non-negative")
        def withDefaultDelay = withDelay(delay)
        def withoutDelay = withDelay(0)
        def withDelay(startingDelay: Double): EventQueue[T] = {
          require(startingDelay >= 0, "Starting delay must be non-negative")
          (1 until n).foldLeft(add(e, startingDelay))((eq, i) => eq.add(e, startingDelay + delay * i))
        }
      }
    }
  }
}
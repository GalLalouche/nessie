package com.nessie.model.eq

import common.rich.RichT._
import common.rich.collections.RichSeq._
import monocle.syntax.{ApplySyntax, FieldsSyntax}

class EventQueue[A] private(
    private val q: List[(A, Double)],
    currentDelay: Double,
    infinites: Map[A, Double],
) extends Iterable[A]
    with ApplySyntax with FieldsSyntax {

  // this ensures by induction that there are no null events
  require(q.isEmpty || q.head._1 != null)

  def this() = this(List[(A, Double)](), 0.0, Map())
  /** Unlike collect, partialMap does not modify elements the partial function does not apply to. */
  def partialMap(f: PartialFunction[A, A]): EventQueue[A] = {
    val orNop: A => A = f.orElse {case e => e}
    def mapTuple(e: (A, Double)): (A, Double) = e.copy(_1 = orNop(e._1))
    new EventQueue(q.map(mapTuple), currentDelay, infinites.map(mapTuple))
  }
  def remove(f: PartialFunction[A, Boolean]): EventQueue[A] = {
    val orKeep: A => Boolean = f.orElse(false.partialConst)
    new EventQueue(q.filterNot(e => orKeep(e._1)), currentDelay, infinites.filterNot(e => orKeep(e._1)))
  }
  override def iterator = new Iterator[A] {
    private var current = EventQueue.this
    override def hasNext: Boolean = current.q.nonEmpty
    override def next(): A = {
      val $ = current.q.head._1
      current = current.tail
      $
    }
  }

  override def toString: String = this.take(3).mkString(", ") + "..."

  def +(e: A): EventQueue[A] = add(e, 0.0)
  def add(e: A, withDelay: Double): EventQueue[A] = add((e, withDelay + currentDelay))
  private def add(eventWithDelay: (A, Double), infinites: Map[A, Double] = this.infinites): EventQueue[A] =
    new EventQueue(
      q.findIndex(_._2 > eventWithDelay._2)
          .map(i => q insert eventWithDelay at i)
          .getOrElse((eventWithDelay :: q.reverse).reverse)
          .toList,
      currentDelay,
      infinites,
    )
  override def tail: EventQueue[A] = {
    val pop = q.head._1
    new EventQueue(q.tail, q.head._2, infinites)
        .mapIf(_ => infinites contains pop) // re-append head if is infinite
        .to(_.repeat(pop).infinitely.inIntervalsOf(infinites(pop)))
  }
  def repeat(e: A) = new {
    def times(n: Int) = new {
      require(n > 0, "The number of repeats must be a positive integer")
      // TODO extract in Intervals of trait
      def inIntervalsOf(delay: Double) = new {
        require(delay >= 0, "The intervals length must non-negative")
        def withDefaultDelay = withDelay(delay)
        def withoutDelay = withDelay(0)
        def withDelay(startingDelay: Double): EventQueue[A] = {
          require(startingDelay >= 0, "Starting delay must be non-negative")
          1.until(n).foldLeft(add(e, startingDelay))((eq, i) => eq.add(e, startingDelay + delay * i))
        }
      }
    }
    def infinitely = new {
      def inIntervalsOf(d: Double): EventQueue[A] = {
        require(d > 0, "Infinite intervals must be positive")
        add((e, d + currentDelay), infinites = infinites + (e -> d))
      }
    }
  }
}

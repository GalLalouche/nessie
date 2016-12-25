package com.nessie.model.eq

import common.rich.RichT._
import common.rich.collections.RichSeq._
import monocle.syntax.{ApplySyntax, FieldsSyntax}

class EventQueue[T <: AnyRef] private(private val q: List[(T, Double)], currentDelay: Double, infinites: Map[T, Double]) extends Iterable[T]
    with ApplySyntax with FieldsSyntax {
  private def qToEQ(eq: (List[(T, Double)], Double, Map[T, Double])) = new EventQueue[T](eq._1, eq._2, eq._3)

  // this ensures by induction that there are no null events
  require(q.isEmpty || q.head._1 != null)

  def this() = this(List[(T, Double)](), 0.0, Map())
  def partialMap(f: PartialFunction[T, T]): EventQueue[T] = {
    val orNop: T => T = f.orElse {
      case e => e
    }
    def mapTuple(e: (T, Double)): (T, Double) = e.copy(_1 = orNop(e._1))
    new EventQueue(q.map(mapTuple), currentDelay, infinites.map(mapTuple))
  }
  def remove(f: PartialFunction[T, Boolean]): EventQueue[T] = {
    val orKeep: T => Boolean = f.orElse {
      case _ => false
    }
    new EventQueue(q.filterNot(e => orKeep(e._1)), currentDelay, infinites.filterNot(e => orKeep(e._1)))
  }
  override def iterator = new Iterator[T] {
    private var current = EventQueue.this
    override def hasNext: Boolean = current.q.nonEmpty
    override def next(): T = {
      val $ = current.q.head._1
      current = current.tail
      $
    }
  }

  override def toString: String = this.take(3).mkString(", ") + "..."

  def +(e: T): EventQueue[T] = add(e, 0.0)
  def add(e: T, withDelay: Double): EventQueue[T] = add((e, withDelay + currentDelay))
  private def add(eventWithDelay: (T, Double), infinites: Map[T, Double] = this.infinites): EventQueue[T] =
    qToEQ((q.findIndex(_._2 > eventWithDelay._2).map(i => q insert eventWithDelay at i).getOrElse((eventWithDelay :: q.reverse).reverse).toList,
        currentDelay,
        infinites))
  override def tail: EventQueue[T] = {
    val pop = q.head._1
    qToEQ((q.tail, q.head._2, infinites))
        .mapIf(_ => infinites contains pop) // re-append head if is infinite
        .to(_.repeat(pop).infinite.inIntervalsOf(infinites(pop)))
  }
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
    def infinite = new {
      def inIntervalsOf(d: Double): EventQueue[T] = {
        require(d > 0, "Infinite intervals must be positive")
        val newMap = infinites + (e -> d)
        add((e, d + currentDelay), newMap)
      }
    }
  }
}

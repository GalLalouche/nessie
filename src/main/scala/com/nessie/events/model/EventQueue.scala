package com.nessie.events.model

import common.rich.collections.RichSeq._
import common.rich.RichT._

class EventQueue[T <: AnyRef] private(private val q: List[(T, Double)], currentDelay: Double, infinites: Map[T, Double]) extends Iterable[T] {
	private implicit def qToEQ(eq: (List[(T, Double)], Double, Map[T, Double])) = new EventQueue[T](eq._1, eq._2, eq._3)

	// this ensures by induction that there are no null events
	require(q.isEmpty || q.head._1 != null)

	def this() = this(List[(T, Double)](), 0.0, Map())
	override def iterator = new Iterator[T] {
		private var current = EventQueue.this
		override def hasNext: Boolean = current.q.nonEmpty
		override def next(): T = {
			val $ = current.q.head._1
			current = current.next
			$
		}
	}

	def +(e: T): EventQueue[T] = add(e, 0.0)
	def add(e: T, withDelay: Double): EventQueue[T] = add((e, withDelay + currentDelay))
	private def add(eventWithDelay: (T, Double), infinites: Map[T, Double] = this.infinites): EventQueue[T] =
		(q
			.findIndex(_._2 > eventWithDelay._2)
			.map(i => q insert eventWithDelay at i)
			.getOrElse((eventWithDelay :: q.reverse).reverse)
			.toList, currentDelay, infinites)
	def next: EventQueue[T] = {
		val pop = q.head._1
		qToEQ((q.tail, q.head._2, infinites))
			.mapIf(e => infinites contains pop) // reappend head if is infinite
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
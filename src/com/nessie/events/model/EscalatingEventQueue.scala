package com.nessie.events.model

import common.rich.collections.RichSeq._

/**
	* Handles events by keeping a "current time" field. Elements added to the queue with delay d, will
	* appear in time currentTime + d. This has two obvious shortcomings:
	* 1. Overflowing
	* 2. For large enough numbers, loss of resolution
	* For all practical uses though, this should suffice.
	*/
class EscalatingEventQueue[T <: AnyRef] private(
	q: Seq[Event[T]], // the current list of events
	currentDelay: Double, // the offset to add to each new element
	infinites: Map[T, Double]) // since we can't keep an infinite sequence, we keep a map of all infinite events
	extends EventQueue[T] {

	def this() = this(List[Event[T]](), 0.0, Map())

	override def add(e: T, withDelay: Double): EscalatingEventQueue[T] = {
		new EscalatingEventQueue[T](addToQueue(e -> withDelay), currentDelay, infinites)
	}

	private def addToQueue(e: Event[T]): Seq[Event[T]] = {
		val updated = e._1 -> (e._2 + currentDelay)
		q
			.findIndex(_._2 > updated._2)
			.map(i => q.insert(updated).at(i))
			.getOrElse(q + updated)
			.toList
	}
	/**
		* adds an infinite number of elements.
		* @param e the element to add
		* @param interval the length of time that must pass between instances of the same event
		* @param delay amount of time that passes before the first occurrence
		*/
	override def addInfinitely(e: T, interval: Double, delay: Double): EventQueue[T] =
		new EscalatingEventQueue[T](addToQueue(e, delay), currentDelay, infinites + (e -> interval))

	override protected def _head: (T, Double) = q.head
	override protected def _isEmpty: Boolean = q.isEmpty
	override protected def _tail: EventQueue[T] = {
		val top = q.head
		val newQueue = new EscalatingEventQueue(q.tail, top._2, infinites)
		infinites // re-add if infinite
			.get(top._1)
			.map(interval => newQueue + (top._1 -> interval))
			.getOrElse(newQueue)
	}
}

package com.nessie.events.model

/**
	* A special priority queue, where each element has a "delay" added to it.
	* Order between elements of the same type is undefined.
	*/
abstract class EventQueue[T <: AnyRef] extends Iterable[Event[T]] {
	protected def _head: Event[T]
	protected def _tail: EventQueue[T]
	protected def _isEmpty: Boolean

	override def tail: EventQueue[T] = _tail

	require(isEmpty || _head._1 != null)
	def iterator = new Iterator[Event[T]] {
		var q: EventQueue[T] = EventQueue.this
		override def hasNext: Boolean = q._isEmpty == false
		override def next(): Event[T] = {
			val $ = q._head
			q = q._tail
			$
		}
	}
	/** Takes all elements in the given window, starting from now */
	def takeWindow(intervalSize: Double) = {
		val now = head._2
		iterator.takeWhile(_._2 < now + intervalSize)
	}

	/** returns an iterator without the time differences */
	def plain: Iterator[T] = iterator.map(_._1)
	/** adds a new element with zero delay */
	def +(e: T): EventQueue[T] = this.+(e -> 0.0)
	/** adds a new element with a delay */
	def +(e: Event[T]): EventQueue[T] = add(e._1, e._2)
	def add(e: T, delay: Double): EventQueue[T]

	/**
		* adds an infinite number of elements.
		* @param e the element to add
		* @param interval the length of time that must pass between instances of the same event
		* @param delay amount of time that passes before the first occurrence
		*/
	def addInfinitely(e: T, interval: Double, delay: Double): EventQueue[T]

	def repeat(e: T) = new {
		def times(n: Int) = new {
			require(n > 0, "The number of repeats must be a positive integer")
			def inIntervalsOf(intervalLength: Double) = new {
				// since this is finite, it is okay to have 0 delay between events
				require(intervalLength >= 0, "The interval length must non-negative")
				def withDefaultDelay = withDelay(intervalLength)
				def withoutDelay = withDelay(0)
				def withDelay(startingDelay: Double): EventQueue[T] = {
					require(startingDelay >= 0, "Starting delay must be non-negative")
					(1 until n).foldLeft(add(e, startingDelay)) {
						(eq, i) => eq + (e -> (startingDelay + intervalLength * i))
					}
				}
			}
		}
		def forever = new {
			def inIntervalsOf(intervalLength: Double) = new {
				require(intervalLength > 0, "Interval length for infinite repeats must be positive")
				def withDefaultDelay = withDelay(intervalLength)
				def withoutDelay = withDelay(0)
				def withDelay(startingDelay: Double): EventQueue[T] = {
					require(startingDelay >= 0, "Starting delay must be non-negative")
					addInfinitely(e, intervalLength, startingDelay)
				}
			}
		}
	}
}

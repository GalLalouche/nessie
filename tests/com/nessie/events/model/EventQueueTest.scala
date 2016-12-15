package com.nessie.events.model

import com.nessie.model.eq.EventQueue
import org.scalatest.{FreeSpec, Matchers}

class EventQueueTest extends FreeSpec with Matchers {
	val eq: EventQueue[String] = new EscalatingEventQueue[String]
	val e = "e"
	val e2 = "e2"
	val e3 = "e3"
	"adding" - {
		"throws exception on null" in {
			an[IllegalArgumentException] should be thrownBy { eq.add(null, 0.5) }
		}
		"no delay" - {
			"should return a queue with only the added element" in {
				(eq + e).plain.toSeq should be === List(e)
			}
		}
		"with delay" - {
			"first with delay, then without should return e2 before e" in {
				(eq.add(e, 0.5) + e2).plain.toSeq should be === List(e2, e)
			}
			"first without delay, then with should return e then e2" in {
				(eq + e add(e2, 0.5)).plain.toSeq should be === List(e, e2)
			}
		}
		"in between" in {
			(eq.add(e, 0).add(e3, 0.5).add(e2, 0.3)).plain.toSeq should be === List(e, e2, e3)
		}
		"after next" in {
			eq.add(e, 0.5).add(e3, 1.5).tail.add(e2, 1.1).plain.toSeq should be === List(e3, e2)
		}
	}
	"adding with repeats" - {
		"finite" - {
			"no other data" in {
				eq.repeat(e).times(3).inIntervalsOf(0).withDefaultDelay.plain.toSeq should be === List(e, e, e)
			}
			"interleaved with other data" in {
				eq.add(e, 0).add(e2, 1).repeat(e3).times(2).inIntervalsOf(0.6).withDefaultDelay.plain.toSeq should be === List(e, e3, e2, e3)
			}
			"interleaved with other data without delay" in {
				eq.add(e, 0.3).add(e2, 0.6).repeat(e3).times(3).inIntervalsOf(0.4).withoutDelay.plain.toSeq should be === List(e3, e, e3, e2, e3)
			}
			"interleaved with other data with non-default delay" in {
				eq.add(e, 0.5).add(e2, 1.0).repeat(e3).times(3).inIntervalsOf(0.3).withDelay(0.6).plain.toSeq should be === List(e, e3, e3, e2, e3)
			}
			"should work on edge cases" in { eq.repeat(e).times(1).inIntervalsOf(0).withoutDelay.plain.toSeq should be === List(e) }
			"throw an exception on illegal inputs" in {
				an[IllegalArgumentException] should be thrownBy { eq.repeat(e).times(0) }
				an[IllegalArgumentException] should be thrownBy { eq.repeat(e).times(-1) }
				an[IllegalArgumentException] should be thrownBy { eq.repeat(e).times(1).inIntervalsOf(-1) }
				an[IllegalArgumentException] should be thrownBy { eq.repeat(e).times(1).inIntervalsOf(0).withDelay(-1) }
			}
		}
		"infinite should" - {
			"throw an exception on illegal inputs" in {
				an[IllegalArgumentException] should be thrownBy { eq.repeat(e).forever.inIntervalsOf(0) }
			}
			"repeat e an infinite number of times" in {
				eq.repeat(e).forever.inIntervalsOf(0.5).withDefaultDelay.take(10).map(_._1).toSeq should be === List.fill(10)(e)
			}
			"with non default delay" in {
				eq
					.add(e, 0.3)
					.repeat(e2).forever.inIntervalsOf(0.2).withDelay(0.4)
					.plain.take(3).toSeq should be === List(e, e2, e2)
			}
			"interleaved" in {
				eq
					.add(e, 0)
					.add(e2, 0.8)
					.repeat(e3).forever.inIntervalsOf(0.3).withDefaultDelay
					.plain.take(6).toSeq should be === List(e, e3, e3, e2, e3, e3)
			}
		}
	}
	"tail should" - {
		"calculate delay correctly" in {
			eq.repeat(e).forever.inIntervalsOf(0.1).withDefaultDelay.add(e2, 0.51).take(6).map(_._1).toSeq should be === List(e, e, e, e, e, e2)
		}
	}
	"takeWindow should" - {
		"get all elements in window" in {
			eq
				.add(e, 0.2)
				.add(e2, 0.4)
				.repeat(e3).forever.inIntervalsOf(0.3).withDefaultDelay
				.takeWindow(1.0)
				.map(_._1).toSeq should be === List(e, e3, e2, e3, e3)
		}
	}
}

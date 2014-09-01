package com.nessie.events.model

import common.AuxSpecs
import org.scalatest.FreeSpec
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._

class EventQueueTests extends FreeSpec with AuxSpecs with MockitoSugar {
	val eq = new EventQueue[Event]
	val e = mock[Event]
	when(e.toString).thenReturn("e")
	val e2 = mock[Event]
	when(e2.toString).thenReturn("e2")
	val e3 = mock[Event]
	when(e3.toString).thenReturn("e3")
	"adding" - {
		"throws exception null" in {
			an[IllegalArgumentException] should be thrownBy {eq + null}
		}
		"no delay" - {
			"should return a queue with only the added element" in {(eq + e).toSeq shouldReturn List(e)}
		}
		"with delay" - {
			"first with delay, then without should return e2 before e" in {(eq.add(e, 0.5) + e2).toSeq shouldReturn List(e2, e)}
			"first without delay, then with should return e then e2" in {(eq + e add(e2, 0.5)).toSeq shouldReturn List(e, e2)}
		}
		"in between" in {(eq.add(e, 0).add(e3, 0.5).add(e2, 0.3)).toSeq shouldReturn List(e, e2, e3)}
		"after next" in {eq.add(e, 0.5).add(e3, 1.5).next.add(e2, 1.1).toSeq shouldReturn List(e3, e2)}
	}
	"adding with repeats" - {
		"finite" - {
			"no other data" in {eq.repeat(e).times(3).inIntervalsOf(0).withDefaultDelay.toSeq shouldReturn List(e, e, e)}
			"interleaved with other data" in {
				eq.add(e, 0).add(e2, 1).repeat(e3).times(2).inIntervalsOf(0.6).withDefaultDelay.toList shouldReturn List(e, e3, e2, e3)
			}
			"interleaved with other data without delay" in {
				eq.add(e, 0.3).add(e2, 0.6).repeat(e3).times(3).inIntervalsOf(0.4).withoutDelay.toList shouldReturn List(e3, e, e3, e2, e3)
			}
			"interleaved with other data with non-default delay" in {
				eq.add(e, 0.5).add(e2, 1.0).repeat(e3).times(3).inIntervalsOf(0.3).withDelay(0.6).toList shouldReturn List(e, e3, e3, e2, e3)
			}
			"should work on edge cases" in {eq.repeat(e).times(1).inIntervalsOf(0).withoutDelay.toSeq shouldReturn List(e)}
			"throw an exception on illegal inputs" in {
				an[IllegalArgumentException] should be thrownBy {eq.repeat(e).times(0)}
				an[IllegalArgumentException] should be thrownBy {eq.repeat(e).times(-1)}
				an[IllegalArgumentException] should be thrownBy {eq.repeat(e).times(1).inIntervalsOf(-1)}
				an[IllegalArgumentException] should be thrownBy {eq.repeat(e).times(1).inIntervalsOf(0).withDelay(-1)}
			}
		}
		"infinite should" - {
			"throw an exception on illegal inputs" in {
				an[IllegalArgumentException] should be thrownBy {eq.repeat(e).infinite.inIntervalsOf(0)}
			}
			"repeat e an infinite number of times" in {
				eq.repeat(e).infinite.inIntervalsOf(0.5).take(10).toSeq shouldReturn List.fill(10)(e)
			}
			"interleaved" in {
				eq.add(e, 0).add(e2, 0.8).repeat(e3).infinite.inIntervalsOf(0.3).take(6).toSeq shouldReturn List(e, e3, e3, e2, e3, e3)
			}
		}
	}
	"next should" - {
		"calculate delay correctly" in {
			eq.repeat(e).infinite.inIntervalsOf(0.1).add(e2, 0.51).take(6).toSeq shouldReturn List(e, e, e, e, e, e2)
		}
	}
}

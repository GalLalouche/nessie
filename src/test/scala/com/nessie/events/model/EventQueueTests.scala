package com.nessie.events.model

import com.nessie.model.eq.EventQueue
import common.AuxSpecs
import org.mockito.Mockito._
import org.scalatest.FreeSpec
import org.scalatest.mockito.MockitoSugar

class EventQueueTests extends FreeSpec with AuxSpecs with MockitoSugar {
  class Event {}
  val eq = new EventQueue[Event]
  val e = mock[Event]
  when(e.toString).thenReturn("e")
  val e2 = mock[Event]
  when(e2.toString).thenReturn("e2")
  val e3 = mock[Event]
  when(e3.toString).thenReturn("e3")
  val e4 = mock[Event]
  when(e4.toString).thenReturn("e4")

  "adding" - {
    "throws exception null" in {
      an[IllegalArgumentException] should be thrownBy {eq + null}
    }
    "no delay" - {
      "should return a queue with only the added element" in {(eq + e).toVector shouldReturn Vector(e)}
    }
    "with delay" - {
      "first with delay, then without should return e2 before e" in {(eq.add(e, 0.5) + e2).toVector shouldReturn Vector(e2, e)}
      "first without delay, then with should return e then e2" in {(eq + e add(e2, 0.5)).toVector shouldReturn Vector(e, e2)}
    }
    "in between" in {eq.add(e, 0).add(e3, 0.5).add(e2, 0.3).toVector shouldReturn Vector(e, e2, e3)}
    "after next" in {eq.add(e, 0.5).add(e3, 1.5).tail.add(e2, 1.1).toVector shouldReturn Vector(e3, e2)}
  }

  "adding with repeats" - {
    "finite" - {
      "no other data" in {eq.repeat(e).times(3).inIntervalsOf(0).withDefaultDelay.toVector shouldReturn Vector(e, e, e)}
      "interleaved with other data" in {
        eq.add(e, 0).add(e2, 1).repeat(e3).times(2).inIntervalsOf(0.6).withDefaultDelay.toVector shouldReturn Vector(e, e3, e2, e3)
      }
      "interleaved with other data without delay" in {
        eq.add(e, 0.3).add(e2, 0.6).repeat(e3).times(3).inIntervalsOf(0.4).withoutDelay.toVector shouldReturn Vector(e3, e, e3, e2, e3)
      }
      "interleaved with other data with non-default delay" in {
        eq.add(e, 0.5).add(e2, 1.0).repeat(e3).times(3).inIntervalsOf(0.3).withDelay(0.6).toVector shouldReturn Vector(e, e3, e3, e2, e3)
      }
      "should work on edge cases" in {eq.repeat(e).times(1).inIntervalsOf(0).withoutDelay.toVector shouldReturn Vector(e)}
      "throw an exception on illegal inputs" in {
        an[IllegalArgumentException] should be thrownBy {eq.repeat(e).times(0)}
        an[IllegalArgumentException] should be thrownBy {eq.repeat(e).times(-1)}
        an[IllegalArgumentException] should be thrownBy {eq.repeat(e).times(1).inIntervalsOf(-1)}
        an[IllegalArgumentException] should be thrownBy {eq.repeat(e).times(1).inIntervalsOf(0).withDelay(-1)}
      }
    }
    "infinite should" - {
      "throw an exception on illegal inputs" in {
        an[IllegalArgumentException] should be thrownBy {eq.repeat(e).infinitely.inIntervalsOf(0)}
      }
      "repeat e an infinite number of times" in {
        eq.repeat(e).infinitely.inIntervalsOf(0.5).take(10).toVector shouldReturn Vector.fill(10)(e)
      }
      "interleaved" in {
        eq.add(e, 0).add(e2, 0.8).repeat(e3).infinitely.inIntervalsOf(0.3).take(6).toVector shouldReturn Vector(e, e3, e3, e2, e3, e3)
      }
    }
  }

  "next" - {
    "calculate delay correctly" in {
      eq.repeat(e).infinitely.inIntervalsOf(0.1).add(e2, 0.51).take(6).toVector shouldReturn Vector(e, e, e, e, e, e2)
    }
  }

  "remove" - {
    def isE(e: Event): PartialFunction[Event, Boolean] = {case x if x == e => true}
    "no such element" in {
      val $ = eq.add(e, 0.5).repeat(e2).infinitely.inIntervalsOf(0.1)
      $.remove(isE(e3)).take(10).toVector shouldReturn $.take(10).toVector
    }
    "element exists" - {
      "single occurrence" in {
        val $ = eq.add(e, 0.5).repeat(e2).infinitely.inIntervalsOf(0.1)
        $.remove(isE(e)).take(10).toVector shouldReturn Vector.fill(10)(e2)
      }
      "multiple occurrences" in {
        val $ = eq
            .repeat(e).times(5).inIntervalsOf(0.3).withoutDelay
            .repeat(e2).infinitely.inIntervalsOf(0.1)
        $.remove(isE(e)).take(10).toVector shouldReturn Vector.fill(10)(e2)
      }
      "infinite occurrences" in {
        val $ = eq
            .repeat(e).times(5).inIntervalsOf(0.3).withoutDelay
            .repeat(e2).infinitely.inIntervalsOf(0.1)
        $.remove(isE(e2)).take(10).toVector shouldReturn Vector.fill(5)(e)
      }
    }
  }

  "partialMap" - {
    def toE4(e: Event): PartialFunction[Event, Event] = {case x if x == e => e4}
    val eToE4 = toE4(e)
    "no such element" in {
      val $ = eq.add(e, 0.5).repeat(e2).infinitely.inIntervalsOf(0.1)
      $.partialMap(toE4(e3)).take(10).toVector shouldReturn $.take(10).toVector
    }
    "element exists" - {
      "single occurrence" in {
        eq.add(e, 0.5).repeat(e2).infinitely.inIntervalsOf(0.2).partialMap(eToE4)
            .take(5).toVector shouldReturn Vector(e2, e2, e4, e2, e2)
      }
      "multiple occurrences" in {
        eq
            .repeat(e).times(2).inIntervalsOf(0.3).withDefaultDelay
            .repeat(e2).infinitely.inIntervalsOf(0.25).partialMap(eToE4)
            .take(6).toVector shouldReturn Vector(e2, e4, e2, e4, e2, e2)
      }
      "infinite occurrences" in {
        eq
            .repeat(e).times(5).inIntervalsOf(0.4).withDefaultDelay
            .repeat(e2).infinitely.inIntervalsOf(0.15).partialMap(eToE4)
            .take(8).toVector shouldReturn Vector(e2, e2, e4, e2, e2, e2, e4, e2)
      }
    }
  }
}

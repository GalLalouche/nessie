package com.nessie.events.model

import common.AuxSpecs
import org.scalatest.FreeSpec
import org.scalatest.mock.MockitoSugar

class EventQueueTests extends FreeSpec with AuxSpecs with MockitoSugar {
  val eq = new EventQueue
  val e = mock[Event]
  "+" - {
    val $ = eq + e
    "no delay" - {
      "should return a queue with the added element" in {$.head shouldReturn e}
      "have size 1" in {$.size shouldReturn 1}
      "next should return an empty EQ" in {$.next.isEmpty shouldReturn true}
    }
    "with delay" - {
      val e2 = mock[Event]
      "first with delay, then without" - {
        val $ = eq.add(e, 0.5) + e2
        "should retain its head if delay is longer than current" in {$.head shouldReturn e2}
        "should have size 2" in {$.size shouldReturn 2}
        "next should return the delayed object" in {$.next.toSeq shouldReturn List(e)}
      }
      "first without delay, then with" - {
        val $ = eq + e add(e2, 0.5)
        "should retain its head if delay is longer than current" in {$.head shouldReturn e}
        "should have size 2" in {$.size shouldReturn 2}
        "next should return the delayed object" in {$.next.toSeq shouldReturn List(e2)}
      }
    }
  }
}

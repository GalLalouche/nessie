package com.nessie.view.zirconview

import com.nessie.view.zirconview.ZirconUtils._
import common.AuxSpecs
import org.hexworks.cobalt.events.api.{CancelledByHand, Subscription}
import org.hexworks.zircon.api.uievent.{MouseEventType, UIEventSource}
import org.mockito.Mockito.{verify, when}
import org.mockito.{Matchers, Mockito}
import org.scalatest.FreeSpec
import org.scalatest.mockito.MockitoSugar
import scalaz.std.VectorInstances
import scalaz.syntax.ToFunctorOps

class ZirconUtilsTest extends FreeSpec with AuxSpecs with MockitoSugar
    with ToFunctorOps with VectorInstances {
  "keyboardActions" - {
    "unsubscribe unregisters" in {
      val source = mock[UIEventSource]
      val o = source.keyboardActions()
      val zirconSub = mock[Subscription]
      when(source.onKeyboardEvent(Matchers.any(), Matchers.any())).thenReturn(zirconSub)
      val s = o.subscribe(_ => ???)
      s.unsubscribe()
      verify(zirconSub, Mockito.times(1)).cancel(CancelledByHand.INSTANCE)
    }
  }

  "mouseActions" - {
    "unsubscribe unregisters" in {
      val source = mock[UIEventSource]
      val o = source.mouseActions()
      val zirconSubs = MouseEventType.values.toVector.fproduct(_ => mock[Subscription]).toMap
      MouseEventType.values
          .foreach(t => when(source.onMouseEvent(Matchers.eq(t), Matchers.any())).thenReturn(zirconSubs(t)))
      val s = o.subscribe(_ => ???)
      s.unsubscribe()
      zirconSubs.values.foreach(verify(_, Mockito.times(1)).cancel(CancelledByHand.INSTANCE))
    }
  }
}

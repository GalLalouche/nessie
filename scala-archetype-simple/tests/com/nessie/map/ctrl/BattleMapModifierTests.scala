package com.nessie.map.ctrl

import org.scalatest.{BeforeAndAfter, OneInstancePerTest, FlatSpec}
import org.scalatest.matchers.ShouldMatchers
import com.nessie.map.view.{CellClicked, MapView}
import tests.MockitoSyrup
import com.nessie.map.model.BattleMap
import scala.swing.{Publisher, Reactor}
import com.nessie.model.map.objects.{EmptyMapObject, BattleMapObject}
import org.mockito.stubbing.Answer
import org.mockito.invocation.InvocationOnMock
import scala.swing.event.Event
import scala.Array
import org.mockito.Mockito

class SwingBattleMapControllerTests extends FlatSpec with ShouldMatchers with MockitoSyrup with
OneInstancePerTest with BeforeAndAfter with Reactor {

	import com.nessie.map._

	var $: SwingBattleMapController = null;
	val p = new Publisher {}
	val v: MapView = mock[MapView]
	val m: BattleMap = mock[BattleMap]
	val event = CellClicked(0, 0)

	when(v.build(any())).thenReturn(v)
	when(v.publish(any())).thenAnswer(new Answer[Unit] {
		def answer(invocation: InvocationOnMock) =
			p.publish(invocation.getArguments()(0).asInstanceOf[Event])
	})

	val o = mock[BattleMapObject]
	when(m.apply((0, 0))).thenReturn(o)
	when(m.apply((1, 1))).thenReturn(EmptyMapObject)
	when(m.width).thenReturn(10)
	when(m.height).thenReturn(5)
	when(m.place(any(), any())).thenReturn(m)
	$ = new SwingBattleMapController(m, v) with Reactor {
		// reroutes listens to p
		override def listenTo(ps: Publisher*) {
			for (pub <- ps if (pub == v)) super.listenTo(p)
		}
	}
	$.startup(Array[String]())

	"Constructor" should "build a map view" in {
		verify(v).build(m)
	}

	"Controller" should "select on click" in {
		p.publish(event)
		verify(v).select((0, 0))
	}

	it should "publish a move request " in {
		v.publish(CellClicked((0, 0)))
		v.publish(CellClicked((1, 1)))
		verify(m, Mockito.atLeastOnce()).apply((0, 0))
		verify(m, Mockito.atLeastOnce()).apply((1, 1))
		verify(m).place((0, 0), EmptyMapObject)
		verify(m).place((1, 1), o)
	}
}


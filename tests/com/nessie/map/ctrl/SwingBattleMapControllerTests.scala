package com.nessie.map.ctrl

import org.scalatest.BeforeAndAfter
import org.scalatest.Finders
import org.scalatest.FlatSpec
import org.scalatest.OneInstancePerTest
import org.scalatest.matchers.ShouldMatchers

import com.nessie.map.model.BattleMap
import com.nessie.map.tupleToMapPoint
import com.nessie.map.view.CellClicked
import com.nessie.map.view.MapActor
import com.nessie.model.map.objects.BattleMapObject
import com.nessie.model.map.objects.EmptyMapObject

import akka.actor.ActorSystem
import akka.testkit.TestActorRef
import akka.testkit.TestKitBase
import akka.testkit.TestProbe
import com.nessie.tests.MockitoSyrup

class SwingBattleMapControllerTests extends FlatSpec with ShouldMatchers with MockitoSyrup with OneInstancePerTest with BeforeAndAfter
	with TestKitBase {
	import com.nessie.map._

	implicit lazy val system = ActorSystem()
	val probe = new TestProbe(system)
	val m: BattleMap = mock[BattleMap]
	val event = CellClicked(0, 0)

	val o = mock[BattleMapObject]
	when(m.apply((0, 0))).thenReturn(o)
	when(m.apply((1, 1))).thenReturn(EmptyMapObject)
	when(m.width).thenReturn(10)
	when(m.height).thenReturn(5)
	when(m.place(any(), any())).thenReturn(m)
	val $ = TestActorRef(new SwingBattleMapController(m) {
		override val mapActor = probe.ref
	})
	$ ! SwingBattleMapController.Startup

	"Startup" should "call GenerateMap on mapview" in {
		$ ! SwingBattleMapController.Startup
		probe expectMsg MapActor.GenerateMap(m)
	}
}


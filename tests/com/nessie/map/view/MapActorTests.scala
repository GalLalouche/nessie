package com.nessie.map.view

import scala.swing.GridPanel
import org.scalatest.BeforeAndAfter
import org.scalatest.Finders
import org.scalatest.FlatSpec
import org.scalatest.OneInstancePerTest
import org.scalatest.matchers.ShouldMatchers
import com.nessie.map.view.swing.MapPanel
import akka.actor.ActorSystem
import akka.testkit.ImplicitSender
import akka.testkit.TestKitBase
import com.nessie.tests.MockitoSyrup
import akka.testkit.TestActorRef
import com.nessie.map.model.BattleMap
import com.nessie.map.ctrl.SwingBattleMapController
import scala.concurrent.duration._
import com.nessie.units.Warrior
import com.nessie.model.map.objects.EmptyMapObject
import akka.actor.Props

class MapActorTests extends FlatSpec with ShouldMatchers with MockitoSyrup with OneInstancePerTest with BeforeAndAfter
	with TestKitBase with ImplicitSender {
	implicit lazy val system = ActorSystem()
	val panel = mock[MapPanel]
	val $ = TestActorRef(Props(new MapActor {
		override val v = panel
	}), testActor, "$")

	"On start" should "send the sender the view" in {
		val c = mock[GridPanel]
		when(panel.generateMap(any())).thenReturn(c)
		$ ! MapActor.GenerateMap(mock[BattleMap])
		expectMsg(SwingBattleMapController.Map(c))
	}

	it should "ignore all other messages" in {
		$ ! CellClicked((0, 0))
		expectNoMsg(0.1 seconds)
	}

	"move" should "be sent after clicking on a unit and then clicking an empty space" in {
		val m = mock[BattleMap]
		when(m height) thenReturn 5
		when(m width) thenReturn 5
		$ ! MapActor.GenerateMap(m)
		when(m.apply((0, 0))).thenReturn(mock[Warrior])
		$ ! CellClicked((0, 0))
		when(m.apply((1, 1))).thenReturn(EmptyMapObject)
		$ ! CellClicked((1, 1))
		fishForMessage(1 seconds) {
			case SwingBattleMapController.Move(x, y) if (x == (0, 0) && y == (1, 1)) => true
			case _ => false
		}
	}

	it should "reset the behavior to accept a generated map" in {
		val m = mock[BattleMap]
		when(m height) thenReturn 5
		when(m width) thenReturn 5
		$ ! MapActor.GenerateMap(m)
		when(m.apply((0, 0))).thenReturn(mock[Warrior])
		$ ! CellClicked((0, 0))
		when(m.apply((1, 1))).thenReturn(EmptyMapObject)
		$ ! CellClicked((1, 1))
		val c = mock[GridPanel]
		when(panel.generateMap(any())).thenReturn(c)
		$ ! MapActor.GenerateMap(mock[BattleMap])
		fishForMessage(1 seconds) {
			case SwingBattleMapController.Map(x) if x == c => true
			case _ => false
		}
	}
}

package com.nessie.map.view.swing

import org.scalatest.{ OneInstancePerTest, FlatSpec }
import org.scalatest.matchers.ShouldMatchers
import tests.{ SwingSpecs, MockitoSyrup }
import scala.swing.Button
import com.nessie.map.model.ArrayBattleMap
import com.nessie.map.view.CellClicked
import akka.testkit.TestActorRef
import akka.actor.ActorSystem
import com.nessie.map.view.MapView
import akka.testkit.TestKitBase
import com.nessie.map.ctrl.SwingBattleMapController
import org.scalatest.BeforeAndAfter
import akka.testkit.TestProbe
import akka.testkit.ImplicitSender
import scala.concurrent.duration._

class MapPanelTests extends FlatSpec with ShouldMatchers with MockitoSyrup with OneInstancePerTest with BeforeAndAfter
	with TestKitBase with ImplicitSender {
	val map = ArrayBattleMap(10, 5)
	implicit lazy val system = ActorSystem()
	val $ = TestActorRef(new MapPanel(new SimpleSwingBuilder))
	
	"GenerateMap" should "generate a component in response" in {
		$ ! MapView.GenerateMap(map)
		expectMsgPF() {
			case SwingBattleMapController.Map(_) => true
		}
	}
	//
	//	it should "publish correct cell location when a button is clicked" in {
	//		$ should publish(CellClicked((1, 1))).on {
	//			$.contents(11).asInstanceOf[Button].doClick
	//		}
	//	}
}
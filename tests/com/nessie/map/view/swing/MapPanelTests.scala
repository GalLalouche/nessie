package com.nessie.map.view.swing

import scala.swing.GridPanel
import scala.swing.event.ActionEvent
import org.scalatest.BeforeAndAfter
import org.scalatest.FlatSpec
import org.scalatest.OneInstancePerTest
import org.scalatest.matchers.ShouldMatchers
import com.nessie.map.model.ArrayBattleMap
import com.nessie.map.model.BattleMap
import akka.actor.ActorSystem
import akka.testkit.ImplicitSender
import akka.testkit.TestKitBase
import akka.testkit.TestProbe
import tests.MockitoSyrup
import com.nessie.map.view.CellClicked

class MapPanelTests extends FlatSpec with ShouldMatchers with MockitoSyrup with OneInstancePerTest with BeforeAndAfter
	with TestKitBase with ImplicitSender {
	val map = ArrayBattleMap(5, 5)
	implicit lazy val system = ActorSystem()
	val probe = new TestProbe(system)
	val grid = new GridPanel(5, 5)
	val $ = new MapPanel(new SimpleSwingBuilder, probe.ref) {
		override def createGridPanel(m: BattleMap): GridPanel = grid
	}
	$.generateMap(map) should be === grid
	"GenerateMap" should "generate a component in response" in {
		$.generateMap(map) should be === grid
	}

	"MapPanel" should "send the owner a message on onclick" in {
		val firstEntry = grid.contents(0)
		firstEntry publish new ActionEvent(firstEntry)
		probe expectMsg CellClicked((0, 0))
	}

	it should "send the owner a message with the correct point" in {
		val firstEntry = grid.contents(7)
		firstEntry publish new ActionEvent(firstEntry)
		probe expectMsg CellClicked((2, 1))
	}

	"select" should "change the color of the cell" in {
		val originalColor = grid.contents(0).background
		$.select((0, 0))
		grid.contents(0).background should not be originalColor
	}

	"unselect" should "change the color back" in {
		val originalColor = grid.contents(0).background
		$.select((0, 0))
		$.unselect
		grid.contents(0).background should be === originalColor
	}
}
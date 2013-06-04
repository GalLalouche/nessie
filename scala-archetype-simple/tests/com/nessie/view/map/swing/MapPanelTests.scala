package com.nessie.view.map.swing

import org.scalatest.BeforeAndAfter
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import com.nessie.model.map.ArrayBattleMap
import tests.{SwingSpecs, MockitoSyrup}
import scala.swing.Button

class MapPanelTests extends FlatSpec with ShouldMatchers with MockitoSyrup with BeforeAndAfter with SwingSpecs {
	var $: MapPanel = null
	val map = ArrayBattleMap(10, 5)
	before({
		$ = new MapPanel(map, new SimpleSwingBuilder)
	})
	//(foo.bar(baz)).bam(bim)
	"Panel" should "publish button clicked when a button is clicked" in {
		$ should publishOn {
			$.contents(0).asInstanceOf[Button].doClick
		}
	}

	it should "publish correct cell location when a button is clicked" in {
		$ should publish(CellClicked((1, 1))).on {
			$.contents(11).asInstanceOf[Button].doClick
		}
	}
}
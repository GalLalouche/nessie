package com.nessie.view.map.swing

import org.scalatest.BeforeAndAfter
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import com.nessie.model.map.{ArrayBattleMap, BattleMap}
import tests.{SwingSpecs, MockitoSyrup}
import scala.swing.{Frame, Button}

class MapPanelTests extends FlatSpec with ShouldMatchers with MockitoSyrup with BeforeAndAfter with SwingSpecs {
	var $: MapPanel = null
	val map = new ArrayBattleMap(10, 5)
	before({
		$ = new MapPanel(map)
	})
	//(foo.bar(baz)).bam(bim)
	"Panel" should "publish button clicked when a button is clicked" in {
		val f = new Frame() {
			listenTo($)
			reactions += {
				case _ => println("hello")
			}
		}
		$ should be;

		$ should publishOn {$.contents(0).asInstanceOf[Button].doClick}
	}
}
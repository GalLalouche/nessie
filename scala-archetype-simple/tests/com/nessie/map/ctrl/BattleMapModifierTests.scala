package com.nessie.map.ctrl

import org.scalatest.{BeforeAndAfter, OneInstancePerTest, FlatSpec}
import org.scalatest.matchers.ShouldMatchers
import com.nessie.map.view.MapView
import tests.MockitoSyrup
import org.mockito.Mockito
import com.nessie.map.model.BattleMap

class SwingBattleMapControllerTests extends FlatSpec with ShouldMatchers with MockitoSyrup with
OneInstancePerTest with BeforeAndAfter {

	var $: SwingBattleMapController = null;
	val v: MapView = mock[MapView]
	val m: BattleMap = mock[BattleMap]
	Mockito.when(v.build(any())).thenReturn(v)
	$ = new SwingBattleMapController(m, v)

	"Constructor" should "build a map view" in {
		verify(v).build(m)
	}
}


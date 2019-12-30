package com.nessie.model.map.fov

import com.nessie.model.map.{BattleMap, MapPoint, VectorGrid}
import org.scalatest.FreeSpec

import common.test.AuxSpecs

class FogOfWarTest extends FreeSpec with AuxSpecs {
  "updateVisible" in {
    val $ = FogOfWar.allHidden(BattleMap.create(VectorGrid, 2, 2))
        .updateVisible(Set(MapPoint(1, 0)))
        .updateVisible(Set(MapPoint(0, 1)))
    $(MapPoint(0, 0)) shouldReturn FogStatus.Hidden
    $(MapPoint(0, 1)) shouldReturn FogStatus.Visible
    $(MapPoint(1, 0)) shouldReturn FogStatus.Fogged
    $(MapPoint(1, 1)) shouldReturn FogStatus.Hidden
  }
}

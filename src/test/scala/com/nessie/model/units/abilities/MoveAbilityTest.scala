package com.nessie.model.units.abilities

import com.nessie.model.map.{DictBattleMap, MapPoint, NonEmptyBattleMapObject}
import common.AuxSpecs
import org.scalatest.FreeSpec

class MoveAbilityTest extends FreeSpec with AuxSpecs {
  "canBeUsed" - {
    "destination is occupied" in {
      val map = DictBattleMap(2, 2).place(MapPoint(1, 1), NonEmptyBattleMapObject)
      MoveAbility(2).canBeUsed(map, MapPoint(0, 0), MapPoint(1, 1)) shouldReturn false
    }
  }
}

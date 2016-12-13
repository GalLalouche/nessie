package com.nessie.units.abilities

import com.nessie.map.model.{ArrayBattleMap, BattleMap, MapPoint, NonEmptyBattleMapObject}
import common.AuxSpecs
import org.scalatest.{FreeSpec, FunSuite}

class MoveAbilityTest extends FreeSpec with AuxSpecs {
  "canBeUsed" - {
    "destination is occupied" in {
      val map = ArrayBattleMap(2, 2).place(MapPoint(1, 1), NonEmptyBattleMapObject)
      new MoveAbility().canBeUsed(map, MapPoint(0, 0), MapPoint(1, 1)) shouldReturn false
    }
  }
}

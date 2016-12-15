package com.nessie.model.units.abilities

import com.nessie.model.map.{ArrayBattleMap, CombatUnitObject, MapPoint}
import com.nessie.model.units.{Skeleton, Warrior}
import common.AuxSpecs
import org.scalatest.FreeSpec

class MeleeAttackTest extends FreeSpec with AuxSpecs {
  "canBeUsed" - {
    "player on enemy" in {
      val map = ArrayBattleMap(2, 2)
          .place(MapPoint(1, 0), CombatUnitObject(new Warrior()))
          .place(MapPoint(1, 1), CombatUnitObject(new Skeleton()))

      val meleeAttack = MeleeAttack(5)
      meleeAttack.canBeUsed(map, MapPoint(1, 0), MapPoint(1, 1)) shouldReturn true
    }
  }
}

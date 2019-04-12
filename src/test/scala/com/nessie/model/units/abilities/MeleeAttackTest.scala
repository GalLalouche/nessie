package com.nessie.model.units.abilities

import com.nessie.model.map.{DictBattleMap, CombatUnitObject, MapPoint}
import com.nessie.model.units.{Skeleton, Warrior}
import common.AuxSpecs
import org.scalatest.FreeSpec

class MeleeAttackTest extends FreeSpec with AuxSpecs {
  "canBeUsed" - {
    "player on enemy" in {
      val map = DictBattleMap(2, 2)
          .place(MapPoint(1, 0), CombatUnitObject(Warrior.create))
          .place(MapPoint(1, 1), CombatUnitObject(Skeleton.create))

      val meleeAttack = MeleeAttack(5)
      CanBeUsed.apply(meleeAttack)(map, MapPoint(1, 0), MapPoint(1, 1)) shouldReturn true
    }
  }
}

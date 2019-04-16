package com.nessie.model.units.abilities

import com.nessie.model.map.{CombatUnitObject, DictBattleMap, MapPoint, NonEmptyBattleMapObject}
import com.nessie.model.units.{Archer, Skeleton, Warrior, Zombie}
import common.AuxSpecs
import org.scalatest.FreeSpec

class CanBeUsedTest extends FreeSpec with AuxSpecs {
  "Movement" - {
    val map = DictBattleMap(2, 2).place(MapPoint(1, 1), NonEmptyBattleMapObject)
    "destination is occupied returns false" in {
      CanBeUsed(MoveAbility(2))(map, MapPoint(0, 0), MapPoint(1, 1)) shouldReturn false
    }
    "destination is free returns true" in {
      CanBeUsed(MoveAbility(2))(map, MapPoint(0, 0), MapPoint(0, 1)) shouldReturn true
    }
  }

  "Attacks" - {
    val map = DictBattleMap(3, 3)
        .place(MapPoint(0, 0), CombatUnitObject(Archer.create))
        .place(MapPoint(1, 0), CombatUnitObject(Warrior.create))
        .place(MapPoint(1, 1), CombatUnitObject(Skeleton.create))
        .place(MapPoint(0, 1), CombatUnitObject(Zombie.create))
        .place(MapPoint(2, 2), CombatUnitObject(Zombie.create))
    "Melee" - {
      val meleeAttack = MeleeAttack(5)
      "player on enemy returns true" in {
        CanBeUsed(meleeAttack)(map, MapPoint(1, 0), MapPoint(1, 1)) shouldReturn true
      }
      "out of range returns false" in {
        CanBeUsed(meleeAttack)(map, MapPoint(1, 0), MapPoint(0, 1)) shouldReturn false
      }
      "enemy on enemy returns false" in {
        CanBeUsed(meleeAttack)(map, MapPoint(1, 1), MapPoint(0, 1)) shouldReturn false
      }
      "attack on empty returns false" in {
        CanBeUsed(meleeAttack)(map, MapPoint(2, 2), MapPoint(2, 1)) shouldReturn false
      }
    }

    "Ranged" - {
      val rangedAttack = RangedAttack(5, range = 2)
      "player on enemy returns true" in {
        CanBeUsed(rangedAttack)(map, MapPoint(0, 0), MapPoint(1, 1)) shouldReturn true
      }
      "out of range returns false" in {
        CanBeUsed(rangedAttack)(map, MapPoint(0, 0), MapPoint(2, 2)) shouldReturn false
      }
      "ally on ally returns false" in {
        CanBeUsed(rangedAttack)(map, MapPoint(0, 0), MapPoint(1, 0)) shouldReturn false
      }
      "attack on empty returns false" in {
        CanBeUsed(rangedAttack)(map, MapPoint(0, 0), MapPoint(2, 0)) shouldReturn false
      }
    }
  }

  "negate" in {
    val map = DictBattleMap(2, 2).place(MapPoint(1, 1), NonEmptyBattleMapObject)
    CanBeUsed.negate(MoveAbility(2))(map, MapPoint(0, 0), MapPoint(1, 1)) shouldReturn true
  }
}

package com.nessie.model.units.abilities

import com.nessie.model.map.{CombatUnitObject, MapPoint, NonEmptyBattleMapObject, VectorBattleMap}
import com.nessie.model.units.{Archer, Skeleton, Warrior, Zombie}
import common.AuxSpecs
import org.scalatest.FreeSpec

class CanBeUsedTest extends FreeSpec with AuxSpecs {
  private val meleeAttack = MeleeAttack(5)
  "Movement" - {
    val map = VectorBattleMap(2, 2).place(MapPoint(1, 1), NonEmptyBattleMapObject)
    "destination is occupied returns false" in {
      CanBeUsed(MoveAbility(2))(map, MapPoint(0, 0), MapPoint(1, 1)) shouldReturn false
    }
    "destination is free returns true" in {
      CanBeUsed(MoveAbility(2))(map, MapPoint(0, 0), MapPoint(0, 1)) shouldReturn true
    }
  }

  "Attacks" - {
    val map = VectorBattleMap(3, 3)
        .place(MapPoint(0, 0), CombatUnitObject(Archer.create))
        .place(MapPoint(1, 0), CombatUnitObject(Warrior.create))
        .place(MapPoint(1, 1), CombatUnitObject(Skeleton.create))
        .place(MapPoint(0, 1), CombatUnitObject(Zombie.create))
        .place(MapPoint(2, 2), CombatUnitObject(Zombie.create))
    "Melee" - {
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

  "getUsablePoints" - {
    val map = VectorBattleMap(3, 3)
        .place(MapPoint(0, 0), CombatUnitObject(Archer.create))
        .place(MapPoint(1, 0), CombatUnitObject(Warrior.create))
        .place(MapPoint(1, 1), CombatUnitObject(Skeleton.create))
        .place(MapPoint(0, 1), CombatUnitObject(Zombie.create))
        .place(MapPoint(2, 2), CombatUnitObject(Zombie.create))
    "empty" in {
      CanBeUsed.getUsablePoints(meleeAttack)(map, MapPoint(2, 2)) shouldBe 'empty
    }
    "single element" in {
      CanBeUsed.getUsablePoints(meleeAttack)(map, MapPoint(0, 0)) shouldReturn Vector(MapPoint(0, 1))
    }
    "range" in {
      CanBeUsed.getUsablePoints(MoveAbility(2))(map, MapPoint(0, 0)) shouldSetEqual Vector(
        MapPoint(0, 2),
        MapPoint(2, 0),
      )
    }
  }

  "negate" in {
    val map = VectorBattleMap(2, 2).place(MapPoint(1, 1), NonEmptyBattleMapObject)
    CanBeUsed.negate(MoveAbility(2))(map, MapPoint(0, 0), MapPoint(1, 1)) shouldReturn true
  }
}

package com.nessie.view

import com.nessie.gm.{Attack, GameState, Movement, MovementWithDistance, PostAction, PreAction}
import com.nessie.model.eq.EventQueue
import com.nessie.model.map.{BattleMap, CombatUnitObject, MapPoint, VectorGrid}
import com.nessie.model.map.fov.FogOfWar
import com.nessie.model.units.{Skeleton, Warrior}
import com.nessie.model.units.abilities.{DamageAbility, MoveAbility}
import common.rich.collections.RichTraversableOnce._
import common.rich.primitives.RichBoolean._
import common.AuxSpecs
import org.scalatest.FreeSpec
import org.scalatest.Inspectors._
import org.scalatest.mockito.MockitoSugar

class ActionMenuHelperTest extends FreeSpec with AuxSpecs with MockitoSugar {
  "getUsablePoints" - {
    "preAction can attack" in {
      val unit = Warrior.create
      val map = BattleMap.create(VectorGrid, 5, 5)
          .place(MapPoint(3, 2), CombatUnitObject(unit))
          .place(MapPoint(3, 3), CombatUnitObject(Skeleton.create))
      val turn = PreAction.empty(unit)
      val gs = GameState(FogOfWar.allVisible(map), EventQueue.empty, Some(turn))
      ActionMenuHelper
          .usableAbilities(gs)(MapPoint(3, 2), MapPoint(3, 3))
          .filter(_._1.isInstanceOf[DamageAbility])
          .single._2 shouldReturn false
    }
    "Can move after action" in {
      val unit = Warrior.create
      val map = BattleMap.create(VectorGrid, 5, 5).place(MapPoint(3, 2), CombatUnitObject(unit))
      val turn = PostAction.apply(unit, Nil, mock[Attack], Nil)
      val gs = GameState(FogOfWar.allVisible(map), EventQueue.empty, Some(turn))
      ActionMenuHelper
          .usableAbilities(gs)(MapPoint(3, 2), MapPoint(2, 3))
          .iterator
          .filter(_._2.isFalse)
          .map(_._1)
          .filter(_.isInstanceOf[MoveAbility])
          .single shouldReturn turn.remainingMovementAbility
    }

    "Can only move remaining amount" in {
      val unit = Warrior.create
      assert(unit.moveAbility.range == 3)
      val map = BattleMap.create(VectorGrid, 5, 5).place(MapPoint(3, 2), CombatUnitObject(unit))
      val turn = PreAction.empty(unit)
          .append(MovementWithDistance(Movement(MapPoint(1, 2), MapPoint(2, 2)), 1))
          .append(mock[Attack])
          .append(MovementWithDistance(Movement(MapPoint(2, 2), MapPoint(3, 2)), 1))
      val gs = GameState(FogOfWar.allVisible(map), EventQueue.empty, Some(turn))
      forAll(ActionMenuHelper
          .usableAbilities(gs)(MapPoint(3, 2), MapPoint(2, 3))) {_._2 shouldReturn true}
    }
  }
}

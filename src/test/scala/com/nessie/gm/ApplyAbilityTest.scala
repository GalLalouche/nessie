package com.nessie.gm

import com.nessie.gm.GameStateChange.ActionTaken
import com.nessie.gm.TurnAction.{ActualAction, MovementAction}
import com.nessie.model.eq.EventQueue
import com.nessie.model.map.{BattleMap, CombatUnitObject, EmptyMapObject, MapPoint, VectorGrid}
import com.nessie.model.units.{Warrior, Zombie}
import org.scalatest.FreeSpec

import common.test.AuxSpecs

class ApplyAbilityTest extends FreeSpec with AuxSpecs {
  "apply" - {
    "ActionTaken" - {
      val unit = Warrior.create
      val enemy = Zombie.create
      val unitObject = CombatUnitObject(unit)
      val action = PreAction.empty(unit)
      val map = BattleMap.create(VectorGrid, 5, 5)
          .place(MapPoint(0, 1), unitObject)
          .place(MapPoint(0, 2), CombatUnitObject(enemy))
      val gs = GameState(
        MapAndFogs.allVisible(map),
        eq = new EventQueue[Event]().add(UnitTurn(unit), withDelay = 1.0),
        currentTurn = Some(action),
      )
      "MovementAction" in {
        val $ = ApplyAbility(ActionTaken(MovementAction(Movement(MapPoint(0, 1), MapPoint(2, 1)))))(gs)
        $.map(MapPoint(0, 1)) shouldReturn EmptyMapObject
        $.map(MapPoint(2, 1)) shouldReturn unitObject
        $.currentTurn.get shouldReturn
            action.append(MovementWithDistance(Movement(MapPoint(0, 1), MapPoint(2, 1)), 2))
      }

      "ActualAction" in {
        val $ = ApplyAbility(ActionTaken(ActualAction(Attack(MapPoint(0, 1), MapPoint(0, 2), 3, 0.5))))(gs)
        $.map(MapPoint(0, 2)).asInstanceOf[CombatUnitObject].unit shouldReturn
            enemy.hitPointsLens.modify(_.reduceHp(3))(enemy)
        $.currentTurn.get shouldReturn action.append(Attack(MapPoint(0, 1), MapPoint(0, 2), 3, 0.5))
      }
    }
  }
}

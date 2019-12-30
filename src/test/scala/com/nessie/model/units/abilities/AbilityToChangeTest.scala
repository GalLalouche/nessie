package com.nessie.model.units.abilities

import com.nessie.gm.{Attack, Movement}
import com.nessie.gm.TurnAction.{ActualAction, MovementAction}
import com.nessie.model.map.MapPoint
import org.scalatest.FreeSpec

import common.test.AuxSpecs

class AbilityToChangeTest extends FreeSpec with AuxSpecs {
  private val src = MapPoint(0, 0)
  private val dst = MapPoint(1, 1)
  "MoveAbility" in {
    AbilityToTurnAction(MoveAbility(3))(src, dst) shouldReturn MovementAction(Movement(src, dst))
  }
  "Damage ability" in {
    AbilityToTurnAction(MeleeAttack(3))(src, dst) shouldReturn ActualAction(Attack(src, dst, 3, 1.0))
  }
}

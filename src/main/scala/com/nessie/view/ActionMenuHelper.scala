package com.nessie.view

import com.nessie.gm.GameState
import com.nessie.model.map.{CombatUnitObject, MapPoint}
import com.nessie.model.units.abilities.{CanBeUsed, MoveAbility, UnitAbility}
import common.rich.primitives.RichBoolean._

import scalaz.std.VectorInstances
import scalaz.syntax.ToFunctorOps

// TODO test
private object ActionMenuHelper
    extends ToFunctorOps with VectorInstances {
  type Disabled = Boolean
  def usableAbilities(gs: GameState)(src: MapPoint, dst: MapPoint): Seq[(UnitAbility, Disabled)] = {
    val unitTurn = gs.currentTurn.get
    val abilities = gs.map(src).asInstanceOf[CombatUnitObject].unit.abilities
    abilities.toVector.fproduct(u => {
      CanBeUsed.negate(u)(gs.map, src, dst) ||
          (u.isInstanceOf[MoveAbility] && unitTurn.remainingMovement == 0 ||
              unitTurn.canAppendAction.isFalse)
    })
  }
}

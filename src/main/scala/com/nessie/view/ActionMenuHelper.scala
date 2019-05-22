package com.nessie.view

import com.nessie.gm.GameState
import com.nessie.model.map.{CombatUnitObject, MapPoint}
import com.nessie.model.units.abilities.{CanBeUsed, MoveAbility, UnitAbility}
import common.rich.RichT._
import common.rich.primitives.RichBoolean._

import scalaz.std.{OptionInstances, VectorInstances}
import scalaz.syntax.ToFunctorOps

private object ActionMenuHelper
    extends ToFunctorOps with VectorInstances with OptionInstances {
  type IsDisabled = Boolean
  def usableAbilities(gs: GameState)(src: MapPoint, dst: MapPoint): Seq[(UnitAbility, IsDisabled)] = {
    val unitTurn = gs.currentTurn.get
    gs.map(src).asInstanceOf[CombatUnitObject].unit
        .abilities.toVector
        .map(_.mapIf(_.isInstanceOf[MoveAbility]).to(unitTurn.remainingMovementAbility))
        .fproduct(ability => {
          CanBeUsed.negate(ability)(gs.map, src, dst) ||
              ability.isInstanceOf[MoveAbility].isFalse && unitTurn.canAppendAction.isFalse
        })
  }
}

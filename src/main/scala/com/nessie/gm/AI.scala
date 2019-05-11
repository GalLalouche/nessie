package com.nessie.gm

import com.nessie.model.units.CombatUnit

private trait AI {
  def apply(u: CombatUnit)(gs: GameState): TurnAction
}

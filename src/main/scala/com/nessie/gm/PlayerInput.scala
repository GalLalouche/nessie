package com.nessie.gm

import com.nessie.model.units.CombatUnit
import scalaz.concurrent.Task

trait PlayerInput {
  def nextState(currentlyPlayingUnit: CombatUnit)(gs: GameState): Task[GameStateChange]
}

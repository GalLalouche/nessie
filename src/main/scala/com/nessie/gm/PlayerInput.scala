package com.nessie.gm

import com.nessie.model.units.CombatUnit
import scalaz.concurrent.Task

// TODO inline this to view?
trait PlayerInput {
  def nextState(currentlyPlayingUnit: CombatUnit)(gs: GameState): Task[GameStateChange]
}

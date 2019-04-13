package com.nessie.gm

import com.nessie.model.units.CombatUnit

import scala.concurrent.Future

trait PlayerInput {
  def nextState(currentlyPlayingUnit: CombatUnit)(gs: GameState): Future[GameStateChange]
}

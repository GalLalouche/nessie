package com.nessie.gm

import com.nessie.units.CombatUnit

import scala.concurrent.Future

trait PlayerInput {
  def nextState(u: CombatUnit)(gs: GameState): Future[GameState]
}

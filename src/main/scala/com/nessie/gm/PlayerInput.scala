package com.nessie.gm

import com.nessie.gm.TurnAction.EndTurn
import com.nessie.model.units.CombatUnit
import scalaz.concurrent.Task

// TODO inline this to view?
trait PlayerInput {
  def nextState(currentlyPlayingUnit: CombatUnit)(gs: GameState): Task[TurnAction]
}

object PlayerInput {
  def fromAI(ai: AI): PlayerInput = new PlayerInput {
    override def nextState(u: CombatUnit)(gs: GameState) = Task.delay(ai(u)(gs))
  }
  val empty: PlayerInput = new PlayerInput {
    override def nextState(currentlyPlayingUnit: CombatUnit)(gs: GameState) = Task now EndTurn
  }
}

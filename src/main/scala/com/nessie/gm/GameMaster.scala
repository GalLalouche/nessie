package com.nessie.gm

import com.nessie.model.units.Owner._

/**
 * A GameMaster runs the game. It keeps the current state of the game, and can fetch the next state, either
 * by making applying a natural event, making an AI movie, or ask the user for input.
 */
private class GameMaster private(playerInput: PlayerInput) {
  private val ai = new CatcherAI

  private def nextState(gs: GameState): (GameStateChange, GameState) = {
    val eq = gs.eq
    val nextEvent: Event = eq.head
    val nextChange: GameStateChange = nextEvent match {
      case UnitTurn(u) => u.owner match {
        case AI => ai(u)(gs)
        case Player => playerInput.nextState(u)(gs).unsafePerformSync
      }
    }
    val nextState = ApplyAbility(nextChange)(gs)
    nextChange -> GameState.eq.modify(_.tail)(nextState)
  }

  def iterator(initialState: GameState): Iterator[(GameStateChange, GameState)] =
    Iterator.iterate[(GameStateChange, GameState)]((NoOp, initialState))(e => nextState(e._2))
}

/** A GameMaster runs the game. It keeps the current state of the game, and can fetch the next state. */
private object GameMaster {
  def initiate(state: GameState, playerInput: PlayerInput): Iterator[(GameStateChange, GameState)] =
    new GameMaster(playerInput).iterator(state)
}

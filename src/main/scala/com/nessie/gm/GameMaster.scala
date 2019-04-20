package com.nessie.gm

import com.nessie.model.units.Owner._
import common.rich.primitives.RichBoolean._
import monocle.syntax.ApplySyntax

/**
 * A GameMaster runs the game. It keeps the current state of the game, and can fetch the next state, either
 * by making applying a natural event, making an AI movie, or ask the user for input.
 */
private class GameMaster private(playerInput: PlayerInput) extends ApplySyntax {
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
    nextChange match {
      case _: Movement if gs.isMidMove.isFalse => nextChange -> nextState.copy(isMidMove = true)
      case _ => nextChange -> nextState.copy(isMidMove = false).&|->(GameState.eq).modify(_.tail)
    }
  }

  def iterator(initialState: GameState): Iterator[(GameStateChange, GameState)] =
    Iterator.iterate[(GameStateChange, GameState)]((NoOp, initialState))(e => nextState(e._2))
}

private object GameMaster {
  def initiate(state: GameState, playerInput: PlayerInput): Iterator[(GameStateChange, GameState)] =
    new GameMaster(playerInput).iterator(state)
}

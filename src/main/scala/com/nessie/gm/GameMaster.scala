package com.nessie.gm

import com.nessie.model.units.Owner._
import common.rich.func.ToMoreFoldableOps
import monocle.syntax.ApplySyntax

import scalaz.std.OptionInstances

/**
 * A GameMaster runs the game. It keeps the current state of the game, and can fetch the next state, either
 * by making applying a natural event, making an AI movie, or ask the user for input.
 */
private class GameMaster private(playerInput: PlayerInput) extends ApplySyntax
    with ToMoreFoldableOps with OptionInstances {
  private val ai = new CatcherAI

  private def nextState(gs: GameState): (GameStateChange, GameState) = {
    val eq = gs.eq
    val nextEvent: Event = eq.head
    val (unit, nextChange) = nextEvent match {
      case UnitTurn(u) => u -> (u.owner match {
        case AI => ai(u)(gs)
        case Player => playerInput.nextState(u)(gs).unsafePerformSync
      })
    }
    val nextState = ApplyAbility(nextChange)(gs)
    nextChange match {
      case m: Movement if gs.midMovement.isEmpty => nextChange -> nextState.copy(midMovement = Some(m))
      case action: UnitAction =>
        val totalDelay = action.turnDelay + gs.midMovement.mapHeadOrElse(_.turnDelay, 0.0)
        nextChange -> nextState
            .copy(midMovement = None)
            .&|->(GameState.eq)
            .modify(_.tail.add(UnitTurn(unit), withDelay = totalDelay))
    }
  }

  def iterator(initialState: GameState): Iterator[(GameStateChange, GameState)] =
    Iterator.iterate[(GameStateChange, GameState)]((NoOp, initialState))(e => nextState(e._2))
}

private object GameMaster {
  def initiate(state: GameState, playerInput: PlayerInput): Iterator[(GameStateChange, GameState)] =
    new GameMaster(playerInput).iterator(state)
}

package com.nessie.gm

import com.nessie.model.units.Owner._
import common.rich.RichFuture._

import scala.concurrent.ExecutionContext.Implicits.global

private class GameMaster private(playerInput: PlayerInput) {
  private val ai = new CatcherAI

  private def nextState(gs: GameState): GameState = {
    val eq = gs.eq
    val nextEvent: Event = eq.head
    val nextState: GameState = nextEvent match {
      case UnitTurn(u) => u.owner match {
        case AI => ai(u)(gs)
        case Player => playerInput.nextState(u)(gs).get
      }
    }
    GameState.eq.modify(_.tail)(nextState)
  }

  def iterator(initialState: GameState): Iterator[GameState] = Stream.iterate(initialState)(nextState).toIterator
}

/** A GameMaster runs the game. It keeps the current state of the game, and can fetch the next state. */
private object GameMaster {
  def initiate(state: GameState, playerInput: PlayerInput): Iterator[GameState] =
    new GameMaster(playerInput).iterator(state)
}

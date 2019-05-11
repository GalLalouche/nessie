package com.nessie.gm

import com.nessie.gm.GameStateChange.{ActionTaken, NoOp}
import com.nessie.gm.TurnAction.{ActualAction, EndTurn, MovementAction, StartTurn}
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
  private val ai = CatcherAI

  private def nextState(gs: GameState): (GameStateChange, GameState) = {
    val eq = gs.eq
    val nextEvent: Event = eq.head
    val (unit, nextAction: TurnAction) = nextEvent match {
      case UnitTurn(u) => (u,
          if (gs.currentTurn.isDefined)
            u.owner match {
              case AI => ai(u)(gs)
              case Player => playerInput.nextState(u)(gs).unsafePerformSync
            }
          else
            StartTurn(u)
      )
    }

    if (nextAction.isInstanceOf[StartTurn]) {
      assert(gs.currentTurn.isEmpty)
      return ActionTaken(nextAction) -> gs.copy(currentTurn = Some(PreAction.empty(unit)))
    }
    val taken = ActionTaken(nextAction)
    taken -> (nextAction match {
      case EndTurn =>
        assert(gs.currentTurn.isDefined)
        gs.copy(currentTurn = None)
            .&|->(GameState.eq)
            .modify(_.tail.add(UnitTurn(unit), withDelay = TurnDelayCalculator(gs.currentTurn.get)))
      case MovementAction(_) | ActualAction(_) => ApplyAbility(taken)(gs)
    })
  }

  def iterator(initialState: GameState): Iterator[(GameStateChange, GameState)] =
    Iterator.iterate[(GameStateChange, GameState)]((NoOp, initialState))(e => nextState(e._2))
}

private object GameMaster {
  def initiate(state: GameState, playerInput: PlayerInput): Iterator[(GameStateChange, GameState)] =
    new GameMaster(playerInput).iterator(state)
}

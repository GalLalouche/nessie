package com.nessie.gm

import com.nessie.model.units.CombatUnit

/**
 * A turn action is made up of individual movements (up to the maximum allowed movement range), interspersed
 * with single UnionAction between them. This is done in order to allow the player to explore a bit before
 * committing to any action.
 * TODO allow the replacing the action with more movement, i.e., a dash move.
 */
sealed trait TurnAction
object TurnAction {
  case object EndTurn extends TurnAction
  // FIXME StartTurn isn't really a TurnAction, since its started by the game and no the agent.
  case class StartTurn(u: CombatUnit) extends TurnAction
  case class MovementAction(m: Movement) extends TurnAction
  // TODO I'm running out of action qualifiers here :|
  case class ActualAction(a: UnitAction) extends TurnAction
}

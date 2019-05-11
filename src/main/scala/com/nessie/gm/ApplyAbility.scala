package com.nessie.gm

import com.nessie.gm.GameStateChange.ActionTaken
import com.nessie.gm.TurnAction.{ActualAction, MovementAction}
import com.nessie.model.map.CombatUnitObject
import monocle.syntax.ApplySyntax

/** Changes the GameState by applying a GameStateChange. Doesn't verify the validity of the change. */
// TODO verify the validity of the change?
private object ApplyAbility extends ApplySyntax {
  def apply(gameStateChange: GameStateChange)(gs: GameState): GameState = gameStateChange match {
    case ActionTaken(a) =>
      val currentTurn = gs.currentTurn.get
      a match {
        case MovementAction(m@Movement(src, dst)) =>
          gs.&|->(GameState.map).modify(_ move src to dst)
              .copy(currentTurn = Some(currentTurn.append(m)))
        case ActualAction(a@Attack(_, dst, damage, _)) =>
          val unit = gs.map(dst).asInstanceOf[CombatUnitObject].unit
          GameState.unitSetter(unit).^|->(unit.hitPointsLens).modify(_.reduceHp(damage))(gs)
              .copy(currentTurn = Some(currentTurn.asInstanceOf[PreAction].append(a)))
      }
  }
}

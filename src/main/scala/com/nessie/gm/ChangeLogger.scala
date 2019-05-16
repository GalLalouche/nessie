package com.nessie.gm

import com.nessie.gm.TurnAction.{ActualAction, EndTurn, MovementAction, StartTurn}

object ChangeLogger {
  def toString(gameStateChange: GameStateChange): Option[String] = gameStateChange match {
    case GameStateChange.NoOp => None
    case GameStateChange.ActionTaken(a) =>
      Some(a match {
        case StartTurn(u) => s"Start of turn for <$u>"
        case EndTurn => s"End of turn"
        case MovementAction(Movement(src, dst)) => s"Moved <$src> to <$dst>"
        case ActualAction(Attack(src, dst, damageAmount, delay)) =>
          s"<$src> attacked <$dst> for <$damageAmount>, delay = <$delay>"
      })
  }
}

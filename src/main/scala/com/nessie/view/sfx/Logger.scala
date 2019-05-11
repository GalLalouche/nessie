package com.nessie.view.sfx

import com.nessie.gm.{Attack, GameStateChange, Movement}
import com.nessie.gm.TurnAction.{ActualAction, EndTurn, MovementAction, StartTurn}
import scalafx.scene.control.Label
import scalafx.scene.layout.VBox

private class Logger {
  val node = new VBox()

  // TODO extract common logic to allow logging in other views.
  def append(gameStateChange: GameStateChange): Unit = gameStateChange match {
    case GameStateChange.NoOp => ()
    case GameStateChange.ActionTaken(a) =>
      val string = a match {
        case StartTurn(u) => s"Start of turn for <$u>"
        case EndTurn => s"End of turn"
        case MovementAction(Movement(src, dst)) => s"Moved <$src> to <$dst>"
        case ActualAction(Attack(src, dst, damageAmount, delay)) =>
          s"<$src> attacked <$dst> for <$damageAmount>, delay = <$delay>"
      }
      node.children.add(new Label(string))
  }
}

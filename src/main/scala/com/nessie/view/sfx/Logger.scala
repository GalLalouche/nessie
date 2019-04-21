package com.nessie.view.sfx

import com.nessie.gm.{Attack, GameStateChange, Movement, NoOp}
import scalafx.scene.control.Label
import scalafx.scene.layout.VBox

private class Logger {
  val node = new VBox()

  def append(gameStateChange: GameStateChange): Unit = {
    if (gameStateChange == NoOp)
      return
    val string = gameStateChange match {
      case Movement(src, dst, delay) => s"Moved <$src> to <$dst>, delay = <$delay>"
      case Attack(src, dst, damageAmount, delay) => s"<$src> attacked <$dst> for <$damageAmount>, delay = <$delay>"
    }
    node.children.add(new Label(string))
  }
}

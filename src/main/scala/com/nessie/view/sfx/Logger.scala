package com.nessie.view.sfx

import com.nessie.gm.{ChangeLogger, GameStateChange}
import scalafx.scene.control.Label
import scalafx.scene.layout.VBox

private class Logger {
  val node = new VBox()

  def append(gameStateChange: GameStateChange): Unit =
    ChangeLogger.toString(gameStateChange).map(new Label(_)).foreach(node.children.add(_))
}

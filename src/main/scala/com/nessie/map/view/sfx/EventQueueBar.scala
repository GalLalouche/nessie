package com.nessie.map.view.sfx

import com.nessie.gm.{Event, GameState, UnitTurn}

import scalafx.scene.Node
import scalafx.scene.control.Label
import scalafx.scene.layout.HBox
import common.rich.RichT._

private class EventQueueBar(gameState: GameState) extends NodeWrapper {
  private def toItem(e: Event): Node = e match {
    case UnitTurn(u) => Label(s"${u.simpleName}'s turn")
  }
  val node = new HBox(10) {
    children = gameState.eq.take(10).map(toItem)
  }
}

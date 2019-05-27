package com.nessie.view.string

import com.nessie.gm.{GameState, GameStateChange, PlayerInput, View}
import com.nessie.model.map.BattleMapPrinter
import com.nessie.model.units.CombatUnit
import scalaz.concurrent.Task

private class StringBattleMapViewer extends View {
  override def updateState(change: GameStateChange, state: GameState): Unit =
    println(BattleMapPrinter(state.map))
  val playerInput: PlayerInput = new PlayerInput {
    override def nextState(u: CombatUnit)(gs: GameState) =
      Task fail new UnsupportedOperationException("StringViewer doesn't support input from player")
  }
}

package com.nessie.map.view.string

import com.nessie.gm.{GameState, View, ViewFactory}

object StringViewFactory extends ViewFactory {
  override def apply(state: GameState): View = new StringBattleMapViewer(state.map)
}

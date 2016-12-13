package com.nessie.gm

trait View extends PlayerInput {
  def updateState(state: GameState): Unit
}

package com.nessie.gm

trait View {
  def updateState(change: GameStateChange, state: GameState): Unit
  def playerInput: PlayerInput
}

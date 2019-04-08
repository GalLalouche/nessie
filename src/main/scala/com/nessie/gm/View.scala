package com.nessie.gm

trait View {
  def updateState(state: GameState): Unit
  def playerInput: PlayerInput
}

package com.nessie.gm

/** Displays the current game state to the user and can fetch input from the user when needed. */
trait View {
  def updateState(change: GameStateChange, state: GameState): Unit
  def playerInput: PlayerInput
}

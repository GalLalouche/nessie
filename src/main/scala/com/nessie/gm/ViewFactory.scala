package com.nessie.gm

trait ViewFactory {
  def apply(state: GameState): View
}

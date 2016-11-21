package com.nessie.gm

trait ViewFactory {
  def apply(nextState: GameState): View
}

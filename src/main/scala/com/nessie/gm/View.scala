package com.nessie.gm

import rx.lang.scala.Observable

trait View extends PlayerInput {
  def updateState(state: GameState): Unit
}

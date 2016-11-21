package com.nessie.gm

import com.nessie.events.model.EventQueue
import com.nessie.map.model.BattleMap

private case class GameState(map: BattleMap, eventQueue: EventQueue[Unit])

private object GameState {
  def fromMap(initialMap: BattleMap): GameState = {
    val turns = initialMap.units
    GameState(initialMap)
  }
}

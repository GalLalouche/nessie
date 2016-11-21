package com.nessie.gm

import com.nessie.ai.CatcherAI
import com.nessie.events.model.EscalatingEventQueue
import com.nessie.map.model.BattleMapController

/** The game master is the main body that handles events from the queue */
class GameMaster(private var q: EscalatingEventQueue[_],
    ai: CatcherAI,
    battleMapController: BattleMapController) {
}
object GameMaster {
  def apply(gameState: GameState) = ???

  def prepDemo
}

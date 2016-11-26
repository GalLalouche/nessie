package com.nessie.gm

import com.nessie.map.model.BattleMap
import com.nessie.units.CombatUnit

trait View {
  def updateState(state: GameState): Unit
  def requirePlayerInput(combatUnit: CombatUnit)(map: BattleMap): BattleMap
}

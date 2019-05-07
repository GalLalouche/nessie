package com.nessie.model.map.gen.mob

import com.nessie.common.rng.Rngable
import com.nessie.model.map.BattleMap

trait MobPlacer {
  def place(battleMap: BattleMap): Rngable[BattleMap]
}

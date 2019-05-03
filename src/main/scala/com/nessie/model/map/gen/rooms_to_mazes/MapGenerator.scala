package com.nessie.model.map.gen.rooms_to_mazes

import com.nessie.common.rng.Rngable
import com.nessie.model.map.BattleMap

trait MapGenerator {
  def generator: Rngable[BattleMap]
}

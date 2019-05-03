package com.nessie.model.map.gen

import com.nessie.common.rng.Rngable
import com.nessie.model.map.BattleMap

trait MapGenerator {
  def generator: Rngable[BattleMap]
}

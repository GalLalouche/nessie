package com.nessie.model.map.gen.mob

import com.nessie.common.rng.Rngable
import com.nessie.model.map.BattleMap
import com.nessie.model.map.gen.MapIterator

trait MobPlacer {
  def place(map: BattleMap): Rngable[BattleMap]
  def iterator(map: BattleMap): MapIterator
}

package com.nessie.model.map.gen

import com.nessie.common.rng.Rngable
import com.nessie.model.map.BattleMap
import common.rich.collections.LazyIterable

trait MapGenerator {
  def generator: Rngable[BattleMap]
  def iterativeGenerator: Rngable[LazyIterable[BattleMap]]
}

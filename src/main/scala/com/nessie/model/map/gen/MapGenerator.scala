package com.nessie.model.map.gen

import com.nessie.common.rng.Rngable
import com.nessie.model.map.BattleMap
import common.rich.collections.LazyIterable

trait MapGenerator {
  def generator: Rngable[BattleMap]
  def iterativeGenerator: Rngable[LazyIterable[BattleMap]]
  // Remove any internal map objects and replace them with the canonical ones, e.g., FullWall and
  // EmptyMapObject.
  def canonize(currentMap: BattleMap): BattleMap
}


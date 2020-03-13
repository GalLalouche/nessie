package com.nessie.model.map.gen

import com.nessie.common.rng.Rngable
import com.nessie.common.rng.Rngable.RngableIterable
import com.nessie.model.map.BattleMap

trait MapIterator {
  def steps: RngableIterable[BattleMap]
  def finalStep: Rngable[BattleMap] = steps.map(_.last)

  // Remove any internal map objects and replace them with the canonical ones, e.g., replace Tunnel and Cave
  // objects with FullWall and EmptyMapObject.
  def canonize(map: BattleMap): BattleMap
}

package com.nessie.model.map.gen.rooms_to_mazes

import com.nessie.common.rng.Rngable.ToRngableOps
import com.nessie.common.rng.StdGen
import com.nessie.model.map.VectorBattleMap

private object RoomsThenMazesGitIgnore extends ToRngableOps {
  def rooms = CreateRooms
      .go(VectorBattleMap(25, 25), 2, 8, 2, 8, 10000, allowAdjacentRooms = false)
      .random(StdGen.fromSeed(2))
}

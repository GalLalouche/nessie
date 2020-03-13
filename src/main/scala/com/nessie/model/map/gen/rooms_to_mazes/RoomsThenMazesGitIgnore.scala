package com.nessie.model.map.gen.rooms_to_mazes

import com.nessie.common.rng.StdGen
import com.nessie.model.map.{BattleMap, VectorGrid}

private object RoomsThenMazesGitIgnore {
  def rooms = CreateRooms
      .go(BattleMap.create(VectorGrid, 25, 25), 2, 8, 2, 8, 10000, allowAdjacentRooms = false)
      .random(StdGen.fromSeed(2))
}

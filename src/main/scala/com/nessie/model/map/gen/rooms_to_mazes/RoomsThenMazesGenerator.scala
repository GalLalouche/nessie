package com.nessie.model.map.gen.rooms_to_mazes

import com.nessie.common.rng.Rngable
import com.nessie.model.map.BattleMap

object RoomsThenMazesGenerator extends MapGenerator {
  override def generator: Rngable[BattleMap] = {
    val (rooms, gen) = RoomsThenMazesGitIgnore.rooms
    ConnectRoomsViaPairs.go(rooms)
  }
}

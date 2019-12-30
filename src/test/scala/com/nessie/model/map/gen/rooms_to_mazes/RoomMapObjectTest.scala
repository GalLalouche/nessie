package com.nessie.model.map.gen.rooms_to_mazes

import com.nessie.model.map.{BattleMap, VectorGrid}
import org.scalatest.FreeSpec

import common.test.AuxSpecs

class RoomMapObjectTest extends FreeSpec with AuxSpecs {
  private def addRoom(map: BattleMap, room: Room, index: Int): BattleMap =
    room.mapPoints.foldLeft(map)(_.place(_, RoomMapObject(index)))
  "getRooms" - {
    val $ = BattleMap.create(VectorGrid, 20, 18)
    "noRooms returns an empty map" in {
      RoomMapObject.getRooms($) shouldBe 'empty
    }
    "single room map returns the room" in {
      val room = Room(1, 2, 3, 4)
      RoomMapObject.getRooms(addRoom($, room, 0)) shouldReturn Map(0 -> room)
    }
    "multiple rooms map returns the room" in {
      val room1 = Room(1, 2, 3, 4)
      val room2 = Room(6, 7, 4, 8)
      RoomMapObject.getRooms(addRoom(addRoom($, room1, 0), room2, 1)) shouldReturn Map(0 -> room1, 1 -> room2)
    }
  }
}

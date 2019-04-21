package com.nessie.model.map.gen

import com.nessie.common.rng.Rngable
import com.nessie.common.rng.Rngable.ToRngableOps
import com.nessie.model.map.{BattleMap, DictBattleMap, Direction, DirectionalMapPoint, MapPoint}
import common.rich.primitives.RichBoolean._
import common.rich.RichT._

import scalaz.syntax.ToMonadOps

/** Creates a BattleMap made up of just non-overlapping rooms. */
class CreateRooms(
    minRoomWidth: Int, maxRoomWidth: Int,
    minRoomHeight: Int, maxRoomHeight: Int,
    mapWidth: Int, mapHeight: Int,
    rooms: List[Room], maxAttempts: Int,
) extends ToMonadOps with ToRngableOps {

  private implicit val rngableRoom: Rngable[Room] = for {
    x <- Rngable.intRange(0, mapWidth)
    y <- Rngable.intRange(0, mapHeight)
    w <- Rngable.intRange(minRoomWidth, maxRoomWidth)
    h <- Rngable.intRange(minRoomHeight, maxRoomHeight)
  } yield Room(x = x, y = y, w = w, h = h)

  private def isValid(room: Room): Boolean = room.x + room.w < mapWidth && room.y + room.h < mapHeight
  private def isNonOverlapping(room: Room): Boolean = rooms.forall(_.noOverlap(room))
  private def addRoom: Rngable[CreateRooms] = for {
    room <- mkRandom[Room]
    nextRooms = if (isValid(room) && isNonOverlapping(room)) room :: rooms else rooms
  } yield new CreateRooms(
    minRoomWidth = minRoomWidth, maxRoomWidth = maxRoomWidth,
    minRoomHeight = minRoomHeight, maxRoomHeight = maxRoomHeight,
    mapWidth = mapWidth, mapHeight = mapHeight,
    rooms = nextRooms, maxAttempts = maxAttempts - 1,
  )

  private def notInRooms(p: MapPoint): Boolean = rooms.forall(_.pointNotInRectangle(p))
  private def inRooms(p: MapPoint): Boolean = notInRooms(p).isFalse
  private def walls(p: MapPoint): Traversable[DirectionalMapPoint] = rooms.flatMap(_.walls(p))
  private def addRooms: BattleMap = {
    println(rooms)
    val emptyMap: BattleMap = DictBattleMap(mapWidth, mapHeight).fillItUp.wallItUp
    //TODO add fold to BattleMap
    val removeFullWalls = emptyMap.points.map(_._1).foldLeft(emptyMap)((map, p) =>
      map.mapIf(inRooms(p)).to(_.remove(p))
    )
    removeFullWalls.betweens.map(_._1).foldLeft(removeFullWalls) {(map, dmp) =>
      if (dmp == DirectionalMapPoint(0, 8, Direction.Right))
        println("FooBar!")
      if (map.isBorder(dmp)) map else {
        val (p1, p2) = dmp.points
        map.mapIf(inRooms(p1) && inRooms(p2)).to(_.remove(dmp))
      }
    }
  }

  def finish: Rngable[BattleMap] =
    if (maxAttempts == 0) Rngable.pure(addRooms) else addRoom.flatMap(_.finish)
}

object CreateRooms {
  def go(
      minRoomWidth: Int, maxRoomWidth: Int,
      minRoomHeight: Int, maxRoomHeight: Int,
      mapWidth: Int, mapHeight: Int,
      maxAttempts: Int,
  ): Rngable[BattleMap] =
    new CreateRooms(
      minRoomWidth: Int, maxRoomWidth: Int,
      minRoomHeight: Int, maxRoomHeight: Int,
      mapWidth: Int, mapHeight: Int,
      rooms = Nil, maxAttempts: Int,
    ).finish
}

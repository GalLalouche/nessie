package com.nessie.model.map.gen

import com.nessie.common.rng.Rngable
import com.nessie.common.rng.Rngable.ToRngableOps
import com.nessie.model.map.{BattleMap, DictBattleMap, Direction, DirectionalMapPoint, MapPoint}
import common.rich.primitives.RichBoolean._
import common.rich.RichT._

import scalaz.syntax.ToMonadOps

/** Creates a BattleMap made up of just non-overlapping rooms. */
private class CreateRooms(
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
  private def inSameRoom(p1: MapPoint, p2: MapPoint): Boolean =
    rooms.exists(r => r.pointInRectangle(p1) && r.pointInRectangle(p2))
  private def addRooms: BattleMap = {
    println(rooms)
    val emptyMap: BattleMap = DictBattleMap(mapWidth, mapHeight).fillItUp.wallItUp
    //TODO add fold to BattleMap
    val removeFullWalls = emptyMap.points.map(_._1).foldLeft(emptyMap)((map, p) =>
      map.mapIf(inRooms(p)).to(_.remove(p))
    )
    removeFullWalls.betweens.map(_._1).foldLeft(removeFullWalls) {(map, dmp) =>
      if (map.isBorder(dmp)) map else {
        val (p1, p2) = dmp.points
        map.mapIf(inSameRoom(p1, p2)).to(_.remove(dmp))
      }
    }
  }

  def roomPoints: Set[MapPoint] = rooms.flatMap(_.mapPoints).toSet

  def finish: Rngable[MapWithRooms] =
    if (maxAttempts == 0) Rngable.pure(MapWithRooms(addRooms, roomPoints)) else addRoom.flatMap(_.finish)
}

private object CreateRooms {
  def go(
      minRoomWidth: Int, maxRoomWidth: Int,
      minRoomHeight: Int, maxRoomHeight: Int,
      mapWidth: Int, mapHeight: Int,
      maxAttempts: Int,
  ): Rngable[MapWithRooms] =
    new CreateRooms(
      minRoomWidth: Int, maxRoomWidth: Int,
      minRoomHeight: Int, maxRoomHeight: Int,
      mapWidth: Int, mapHeight: Int,
      rooms = Nil, maxAttempts: Int,
    ).finish
}

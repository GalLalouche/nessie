package com.nessie.model.map.gen

import com.nessie.common.rng.Rngable
import com.nessie.common.rng.Rngable.ToRngableOps
import com.nessie.model.map.{BattleMap, MapPoint}
import common.rich.collections.RichSeq._
import common.rich.primitives.RichBoolean._
import common.rich.RichT._
import common.rich.func.ToMoreFoldableOps

import scalaz.std.OptionInstances
import scalaz.syntax.ToMonadOps

/** Creates a BattleMap made up of just non-overlapping rooms. */
private object CreateRooms
    extends ToMoreFoldableOps with OptionInstances {
  def go(
      initialMap: BattleMap,
      minRoomWidth: Int, maxRoomWidth: Int,
      minRoomHeight: Int, maxRoomHeight: Int,
      maxAttempts: Int,
  ): Rngable[BattleMap] = {
    implicit val rngableRoom: Rngable[Room] = for {
      x <- Rngable.intRange(0, initialMap.width)
      y <- Rngable.intRange(0, initialMap.height)
      w <- Rngable.intRange(minRoomWidth, maxRoomWidth)
      h <- Rngable.intRange(minRoomHeight, maxRoomHeight)
    } yield Room(x = x, y = y, w = w, h = h)
    Aux(
      mapWidth = initialMap.width, mapHeight = initialMap.height,
      rooms = Nil, maxAttempts = maxAttempts,
    ).finish.map(buildMap(initialMap))
  }

  private case class Aux(
      mapWidth: Int, mapHeight: Int,
      rooms: List[Room], maxAttempts: Int,
  )(implicit rngableRoom: Rngable[Room]) extends ToMonadOps with ToRngableOps {
    private def isValid(room: Room): Boolean = room.x + room.w < mapWidth && room.y + room.h < mapHeight
    private def isNonOverlapping(room: Room): Boolean = rooms.forall(_.noOverlap(room))
    // TODO lenses?
    private def addRoom: Rngable[Aux] = mkRandom[Room].map(room => copy(
      rooms = rooms.mapIf(isValid(room) && isNonOverlapping(room)).to(room :: _),
      // TODO don't increase maxAttempts if room was successfully added?
      maxAttempts = maxAttempts - 1,
    ))

    def finish: Rngable[Seq[Room]] = if (maxAttempts == 0) Rngable.pure(rooms) else addRoom.flatMap(_.finish)
  }

  private def buildMap(initialMap: BattleMap)(rooms: Seq[Room]): BattleMap = {
    def roomIndex(p: MapPoint): Option[RoomMapObject] =
      rooms.findIndex(_.pointInRectangle(p)).map(RoomMapObject)
    def inSameRoom(p1: MapPoint, p2: MapPoint): Boolean =
      rooms.exists(r => r.pointInRectangle(p1) && r.pointInRectangle(p2))

    val removeFullWallsInRooms = initialMap.clearAllPoints.fillItAll.wallItUp
        .foldPoints((map, p) => roomIndex(p).mapHeadOrElse(map.replace(p, _), map))
    val removeWallsInsideRooms = removeFullWallsInRooms.foldBetweenPoints((map, dmp) =>
      map mapIf map.isBorder(dmp).isFalse to {
        val (p1, p2) = dmp.points
        map.mapIf(inSameRoom(p1, p2)).to(_.remove(dmp))
      })
    removeWallsInsideRooms
  }
}

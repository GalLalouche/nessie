package com.nessie.model.map.gen.rooms_to_mazes

import com.nessie.common.rng.Rngable
import com.nessie.common.rng.Rngable.ToRngableOps
import com.nessie.model.map.{BattleMap, FullWall, MapPoint}
import common.rich.collections.RichSeq._
import common.rich.collections.RichTraversableOnce._
import common.rich.RichT._
import common.rich.func.ToMoreFoldableOps
import scalaz.std.OptionInstances
import scalaz.syntax.ToMonadOps

/**
 * Creates a BattleMap made up of just non-overlapping rooms. At the end of this procedure the resulting
 * [[BattleMap]] will be made up of [[FullWall]]s and [[RoomMapObject]]s.
 */
private object CreateRooms
    extends ToMoreFoldableOps with OptionInstances {
  def go(
      initialMap: BattleMap,
      minRoomWidth: Int, maxRoomWidth: Int,
      minRoomHeight: Int, maxRoomHeight: Int,
      maxAttempts: Int,
      // If true, rooms can be next to each other, which will result in a bigger, not necessarily rectangular
      // room. If false, Rooms must have at least one full tile gap between them, including diagonals (i.e.,
      // rooms cannot touch corners).
      allowAdjacentRooms: Boolean
  ): Rngable[BattleMap] = {
    implicit val rngableRoom: Rngable[Room] = for {
      x <- Rngable.intRange(0, initialMap.width)
      y <- Rngable.intRange(0, initialMap.height)
      w <- Rngable.intRange(minRoomWidth, maxRoomWidth)
      h <- Rngable.intRange(minRoomHeight, maxRoomHeight)
    } yield Room(x = x, y = y, w = w, h = h)
    Aux(
      mapWidth = initialMap.width, mapHeight = initialMap.height,
      rooms = Nil, maxAttempts = maxAttempts, allowAdjacentRooms = allowAdjacentRooms,
    ).finish.map(buildMap(initialMap))
  }

  private case class Aux(
      mapWidth: Int, mapHeight: Int,
      rooms: List[Room], maxAttempts: Int,
      allowAdjacentRooms: Boolean,
  )(implicit rngableRoom: Rngable[Room]) extends ToMonadOps with ToRngableOps {
    private def isValid(room: Room): Boolean = room.x + room.w < mapWidth && room.y + room.h < mapHeight
    private def isNonOverlapping(room: Room): Boolean =
      rooms.fornone(_.isOverlapping(room)) && (allowAdjacentRooms || rooms.fornone(_.isAdjacent(room)))
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
      rooms.findIndex(_.pointInRectangle(p)).map(RoomMapObject.apply)

    initialMap.fill(FullWall).mapPoints(roomIndex(_).getOrElse(_))
  }
}

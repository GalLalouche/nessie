package com.nessie.model.map.gen.robots

import com.nessie.common.rng.Rngable
import com.nessie.common.rng.Rngable.ToRngableOps._
import com.nessie.model.map.{BattleMap, Direction, GridSize, MapPoint}

import scalaz.syntax.monadPlus._
import scalaz.syntax.semigroup.ToSemigroupOps

import common.rich.primitives.RichBoolean

/** Builds a room to the side of some path. */
private case class Roomer(
    doorLocation: MapPoint,
    doorPosition: Direction,
    doorRelativeLocationFromCorner: Int,
    roomSize: GridSize,
    doorWidth: Int,
    generation: Int,
) extends Robot {
  assert(doorWidth == 1)
  override def go(map: BattleMap) = {
    val (minX, minY) = {
      val roomPoint = doorLocation.go(doorPosition.opposite)
      if (doorPosition.isVertical) {
        val minX = roomPoint.go(Direction.Left, doorRelativeLocationFromCorner).x
        val minY = roomPoint.y
        minX -> minY
      } else {
        val minX = roomPoint.x
        val minY = roomPoint.go(Direction.Up, doorRelativeLocationFromCorner).y
        minX -> minY
      }
    }
    val maxX = minX + roomSize.width - 1
    val maxY = minY + roomSize.height - 1
    val points: Iterable[MapPoint] = MapPoint.squareInclusive(minX, minY, maxX, maxY)
    // A room can build an extra door and continue a tunnel from there.
    val tunneler: Rngable[Robot] = (for {
      create <- Rngable.boolean(1.0).liftSome
      if create
      doorPosition <- mkRandom[Direction].liftSome
      doorRelativeLocationFromCorner <- Rngable.intRange(1, 3).liftSome
      length <- Tunneler.TunnelLength.liftSome
      door = doorPosition match {
        case Direction.Up => MapPoint(minX + doorRelativeLocationFromCorner, minY - 1)
        case Direction.Down => MapPoint(minX + doorRelativeLocationFromCorner, maxY + 1)
        case Direction.Left => MapPoint(minX - 1, minY + doorRelativeLocationFromCorner)
        case Direction.Right => MapPoint(maxX + 1, minY + doorRelativeLocationFromCorner)
      }
      if map isEmptyAt door
    } yield Robot(_.place(door, Door(generation + 1))) ⊹ new Tunneler(
      startingPosition = door.go(doorPosition),
      direction = doorPosition,
      tunnelWidth = 1,
      tunnelLength = length,
      generation = generation + 1,
    )).orZero
    Rngable.when(points.forall(RichBoolean.and(map.isInBounds, map.isEmptyAt))) {
      val nextMap: BattleMap =
        points.foldLeft(map.place(doorLocation, Door(generation)))(_.place(_, Room(generation)))
      tunneler.map(nextMap.->)
    }
  }
}

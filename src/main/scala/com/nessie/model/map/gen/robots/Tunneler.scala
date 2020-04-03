package com.nessie.model.map.gen.robots

import com.nessie.common.rng.Rngable
import com.nessie.common.rng.Rngable.RngableOption
import com.nessie.common.rng.Rngable.ToRngableOps._
import com.nessie.model.map.{BattleMap, Direction, GridSize, MapPoint}
import com.nessie.model.map.gen.robots.Tunneler._

import scala.annotation.tailrec

import scalaz.std.vector.vectorInstance
import scalaz.syntax.monadPlus._
import common.rich.func.RichOptionT._
import common.rich.func.ToMoreFoldableOps._

import common.rich.RichT._

/** A tunneler is the most basic Robot. It digs tunnels, and can spawn the other robots. */
private case class Tunneler(
    startingPosition: MapPoint, direction: Direction, tunnelWidth: Int, tunnelLength: Int, generation: Int
) extends Robot {
  private val otherDirections = Direction.values.filter(_ != direction.opposite)

  override def go(map: BattleMap): RngableOption[(BattleMap, Robot)] = for {
    _ <- Rngable.some()
    if generation < 30 && map.isInBounds(startingPosition)
    preOccupied = map.isOccupiedAt(startingPosition)
    actualStartingPlace = startingPosition.mapIf(preOccupied).to(startingPosition.go(direction))
    if map.isInBounds(actualStartingPlace) && map.isEmptyAt(actualStartingPlace)
    actualLength = tunnelLength.mapIf(preOccupied).to(tunnelLength - 1)
    _ = assert(map.isEmptyAt(actualStartingPlace))
    _ = assert(actualLength >= 0)
    (resultMap, endPosition) = go(map, actualStartingPlace, actualLength)
    result <- Vector(roomer _, junctioner _, tunneler _)
        .map(_ (endPosition))
        .asum
        .map(resultMap -> _)
        .liftSome
  } yield result

  @tailrec private def go(map: BattleMap, position: MapPoint, remainingLength: Int): (BattleMap, MapPoint) =
    if (remainingLength == -1)
      map -> position
    else if (map.isOutBounds(position) || map.isOccupiedAt(position))
      map -> position.go(direction.opposite)
    else
      go(
        map = map.place(position, Tunnel(generation)),
        position = position go direction,
        remainingLength = remainingLength - 1,
      )
  private def tunneler(endPosition: MapPoint): Rngable[Robot] = for {
    nextDirection <- Rngable.sample(otherDirections)
    nextLength <- TunnelLength
  } yield new Tunneler(
    startingPosition = endPosition,
    direction = nextDirection,
    tunnelWidth = 1,
    tunnelLength = nextLength,
    generation = generation + 1
  )
  private def roomer(endPosition: MapPoint): Rngable[Robot] = Rngable.withProbability[Robot](0.8) {
    val roomSide = Rngable.intRange(3, 7)
    for {
      width <- roomSide
      height <- roomSide
      doorPosition <- Rngable.sample(otherDirections)
    } yield Roomer(
      doorLocation = endPosition.go(direction.opposite).go(doorPosition.opposite),
      doorPosition = doorPosition,
      doorRelativeLocationFromCorner = 2,
      roomSize = GridSize(width, height),
      doorWidth = 1,
      generation = generation + 1,
    )
  } |||| tunneler(endPosition)
  private def junctioner(endPosition: MapPoint): Rngable[Robot] = Rngable.withProbability[Robot](0.2) {
    Rngable.intRange(tunnelWidth, tunnelWidth).map(r => Junctioner(endPosition, r, generation + 1))
  } |||| tunneler(endPosition)
}

private object Tunneler {
  val TunnelLength = Rngable.intRange(5, 15)
  /** The first tunneler starts somewhere on the edge of the map. */
  def apply(map: BattleMap): Rngable[Tunneler] = for {
    startingSide <- mkRandom[Direction]
    startingDirection <- Rngable.sample(Direction.values.filter(_ != startingSide))
    startingLocation <- Rngable.intRange(0, if (startingSide.isVertical) map.width else map.height)
    tunnelLength <- TunnelLength
  } yield new Tunneler(
    startingPosition = startingSide match {
      case Direction.Up => MapPoint(x = startingLocation, y = 0)
      case Direction.Down => MapPoint(startingLocation, map.height - 1)
      case Direction.Left => MapPoint(0, startingLocation)
      case Direction.Right => MapPoint(map.width - 1, startingLocation)
    },
    direction = startingDirection,
    tunnelWidth = 1,
    tunnelLength = tunnelLength,
    generation = 0,
  )
}

package com.nessie.model.map.gen

import com.nessie.common.rng.Rngable
import common.rich.primitives.RichBoolean._
import com.nessie.common.Percentage
import com.nessie.model.map.{BattleMap, BattleMapObject, DirectionalMapPoint, MapPoint, Wall}
import com.nessie.model.map.gen.ConnectRoomsAndMazes._
import common.rich.RichT._
import common.rich.RichTuple._

import scala.annotation.tailrec

import scalaz.std.VectorInstances
import scalaz.syntax.ToTraverseOps

/**
 * Outline of algorithm:
 *
 * Pick a random room point
 * Mark said point as Reachable
 * While (non-Reachable points set is not empty):
 *   Mark all points reachable from a reachable point as reachable
 *   Let ps be the set of non-reachable points that are adjacent to reachable point; since these points are
 *   non reachable, we can assume there is a wall between these points and a reachable point.
 *   Pick a random point p in ps and tear down the wall between p and its reachable neighbor
 *   Pick all other points in p with probability *additionalPathProbability* and break down their walls
 * </p>
 */
private class ConnectRoomsAndMazes(
    map: BattleMap,
    additionalPathProbability: Percentage,
    index: Int,
) extends ToTraverseOps with VectorInstances {
  def finish: Rngable[BattleMap] = {
    val walls = perimeterWalls(map)
    if (walls.isEmpty) Rngable.pure(map) else for {
      wall <- Rngable.sample(walls)
      otherWalls <- Rngable.keepWithProbability(additionalPathProbability, walls.toList)
      nextPoint = wall.points.mapTo {case (x, y) => if (map(x) |> isReachable) y else x}
      nextMap = otherWalls.iterator.filter(_ != wall).foldLeft(map.remove(wall))(_.remove(_))
      result <- new ConnectRoomsAndMazes(
        markReachable(nextMap, List(nextPoint), index, Set()), additionalPathProbability, index + 1).finish
    } yield result
  }
}

private object ConnectRoomsAndMazes {
  private def isReachable(o: BattleMapObject) = o.isInstanceOf[ReachableMapObject]
  def go(map: BattleMap, additionalPathProbability: Percentage): Rngable[BattleMap] = for {
    firstPoint <- Rngable.sample(map.points.view.filter(_._2.isInstanceOf[RoomMapObject]).map(_._1).toVector)
    firstPointMarked = map.replace(firstPoint, ReachableMapObject(0))
    allMarked = markReachable(firstPointMarked, map.reachableNeighbors(firstPoint).toList, 1, Set())
    result <- new ConnectRoomsAndMazes(
      map = allMarked,
      additionalPathProbability = additionalPathProbability,
      index = 2,
    ).finish
  } yield result

  // TODO I should really start using plain old graph algorithms for this :|
  @tailrec
  private def markReachable(
      map: BattleMap, queue: List[MapPoint], index: Int, visited: Set[MapPoint]): BattleMap = {
    if (queue.isEmpty) map else {
      val next :: tail = queue
      if (visited(next)) markReachable(map, tail, index, visited) else {
        val nextPoints = map.reachableNeighbors(next).toList ++ tail
        val nextMap = map.mapIf(map(next).|>(isReachable).isFalse).to(_.replace(next, ReachableMapObject(index)))
        markReachable(nextMap, nextPoints, index, visited + next)
      }
    }
  }

  /** There perimeter walls are the all the walls that are between a reachable and a non-reachable point. */
  private def perimeterWalls(map: BattleMap): Vector[DirectionalMapPoint] = map.betweens.view
      .filter(_._2 == Wall)
      .map(_._1)
      .filterNot(map.isBorder)
      .filter {dmp =>
        val (p1, p2) = dmp.points.map(map.apply)
        p1.|>(isReachable) && p2.|>(isReachable).isFalse || p2.|>(isReachable) && p1.|>(isReachable).isFalse
      }.toVector
}

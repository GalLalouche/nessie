//package com.nessie.model.map.gen
//
//import com.nessie.common.rng.Rngable
//import com.nessie.common.Percentage
//import com.nessie.model.map.{BattleMap, BattleMapObject, DirectionalMapPoint, MapPoint, Wall}
//import common.rich.primitives.RichBoolean._
//import common.rich.RichT._
//import common.rich.RichTuple._
//
//import scala.annotation.tailrec
//
//import scalaz.std.vector.vectorInstance
//import scalaz.syntax.traverse.ToTraverseOps
//
///**
// * Outline of algorithm:
// *
// * Pick a random room point
// * Mark said point as Reachable
// * While (non-Reachable points set is not empty):
// *   Mark all points reachable from a reachable point as reachable
// *   Let ps be the set of non-reachable points that are adjacent to reachable point; since these points are
// *   non reachable, we can assume there is a wall between these points and a reachable point.
// *   Pick a random point p in ps and tear down the wall between p and its reachable neighbor
// *   Pick all other points in p with probability *additionalPathProbability* and break down their walls
// *
// * At the end of this procedure the resulting [[BattleMap]] will be strongly connected, with connectivity
// * level dependent on the additionalPathProbability parameter. All the objects in the map will be
// * [[ReachableMapObject]]s.
// */
//private object ConnectRoomsAndMazes {
//  def wrap(o: BattleMapObject, index: Int) = ReachableMapObject(o.asInstanceOf[AlgorithmStepMapObject], index)
//  def go(map: BattleMap, additionalPathProbability: Percentage): Rngable[BattleMap] = for {
//    firstPoint <- Rngable.sample(map.objects.view.filter(_._2.isInstanceOf[RoomMapObject]).map(_._1).toVector)
//    firstPointMarked = map.replace(firstPoint, wrap(map(firstPoint), 0))
//    allMarked = markReachable(firstPointMarked, map.reachableNeighbors(firstPoint).toList, 1, Set())
//    result <- new Aux(
//      map = allMarked,
//      additionalPathProbability = additionalPathProbability,
//      index = 2,
//    ).finish
//  } yield result
//
//  private class Aux(
//      map: BattleMap,
//      additionalPathProbability: Percentage,
//      index: Int,
//  ) {
//    def finish: Rngable[BattleMap] = {
//      val walls = perimeterWalls(map)
//      if (walls.isEmpty) Rngable.pure(map) else for {
//        wall <- Rngable.sample(walls)
//        otherWalls <- Rngable.keepWithProbability(additionalPathProbability, walls.toList)
//        nextPoint = wall.points.mapTo {case (x, y) => if (map(x) |> isReachable) y else x}
//        nextMap = otherWalls.iterator.filter(_ != wall).foldLeft(map remove wall)(_ remove _)
//        result <- new Aux(
//          markReachable(nextMap, List(nextPoint), index, Set()), additionalPathProbability, index + 1).finish
//      } yield result
//    }
//  }
//
//  private def isReachable(o: BattleMapObject) = o.isInstanceOf[ReachableMapObject]
//  private def isNotReachable(o: BattleMapObject) = isReachable(o).isFalse
//
//  // TODO I should really start using plain old graph algorithms for this :|
//  @tailrec
//  private def markReachable(
//      map: BattleMap, queue: List[MapPoint], index: Int, visited: Set[MapPoint]): BattleMap = {
//    if (queue.isEmpty) map else {
//      val next :: tail = queue
//      if (visited(next)) markReachable(map, tail, index, visited) else {
//        val nextPoints = map.reachableNeighbors(next).toList ++ tail
//        val nextMap = map.mapIf(map(next).|>(isNotReachable)).to(_.replace(next, wrap(map(next), index)))
//        markReachable(nextMap, nextPoints, index, visited + next)
//      }
//    }
//  }
//
//  /** There perimeter walls are the all the walls that are between a reachable and a non-reachable point. */
//  private def perimeterWalls(map: BattleMap): Vector[DirectionalMapPoint] = map.betweenObjects.view
//      .filter(_._2 == Wall)
//      .map(_._1)
//      .filterNot(map.isBorder)
//      .filter {dmp =>
//        val (p1, p2) = dmp.points.map(map.apply)
//        p1.|>(isReachable) && p2.|>(isNotReachable) || p2.|>(isReachable) && p1.|>(isNotReachable)
//      }.toVector
//}

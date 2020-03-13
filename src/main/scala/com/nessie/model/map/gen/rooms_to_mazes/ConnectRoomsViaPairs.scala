package com.nessie.model.map.gen.rooms_to_mazes

import com.nessie.common.graph.DfsTraversal
import com.nessie.common.graph.RichUndirected._
import com.nessie.common.rng.Rngable
import com.nessie.common.rng.Rngable.{RngableIterable, RngableOption}
import com.nessie.common.rng.Rngable.ToRngableOps._
import com.nessie.model.map.{BattleMap, FullWall, MapPoint}

import scalaz.syntax.functor.ToFunctorOps
import common.rich.func.MoreIterableInstances._
import common.rich.func.MoreSeqInstances._
import common.rich.func.ToMoreMonadPlusOps._

import common.rich.RichT._
import common.rich.RichTuple._
import common.rich.collections.LazyIterable
import common.rich.collections.RichTraversableOnce._
import common.uf.ImmutableUnionFind

/**
 * Connects rooms by iterating over pairs of unconnected rooms and digging a random path from one room to
 * another. Completes when all rooms are connected together, at which point the resulting [[BattleMap]] will
 * be made up of [[RoomMapObject]]s, [[TunnelMapObject]]s, and [[FullWall]]s.
 */
private object ConnectRoomsViaPairs {
  def go(map: BattleMap): Rngable[BattleMap] = iterate(map).map(_.last)

  def iterate(map: BattleMap): RngableIterable[BattleMap] = Rngable.iterateOptionally(
    new Aux(
      map = map,
      rooms = ImmutableUnionFind(
        map.objects.view.map(_._2).select[RoomMapObject].map(_.index).toSet),
      index = 0,
    )
  )(_.next).map(_.map(_.map))

  private class Aux(
      val map: BattleMap,
      rooms: ImmutableUnionFind[Int],
      index: Int,
  ) {
    def next: RngableOption[Aux] = if (rooms.numberOfSets == 1) Rngable.pure(None) else for {
      (r1, r2) <- rooms.values.unorderedPairs.toIterator.filterNot(_ reduce rooms.sameSet).sample
      (map, rooms) <- Connector(map, rooms, r1, r2, RoomMapObject.getRooms(map), index).go
    } yield Some(new Aux(map, rooms, index + 1))
  }

  private case class Connector(
      map: BattleMap, roomConnections: ImmutableUnionFind[Int], r1: Int, r2: Int, rooms: Map[Int, Room],
      index: Int) {
    def go: Rngable[(BattleMap, ImmutableUnionFind[Int])] =
      if (roomConnections.sameSet(r1, r2)) Rngable.pure(map -> roomConnections) else {
        val startingPoints = {
          val room2 = rooms(r2)
          val sorted = getPerimeter(r1).fproduct(room2.distanceTo).sortBy(_.swap)
          val min = sorted.head._2
          sorted.takeWhile(_._2 == min).map(_._1)
        }
        for {
          startingPoint <- startingPoints.sample
          points = rooms(r1).mapPoints
          path: LazyIterable[MapPoint] <- DfsTraversal(map.toFullGraph.removeNodes(points), startingPoint)
        } yield {
          val fullWalls = path.takeUntil(keepMoving).toVector
          val connectedRooms = map.neighbors(fullWalls.last)
              .map(map.apply)
              .select[RoomMapObject]
              .map(_.index)
              .toVector
          val nextMap = fullWalls.foldLeft(map)(_.place(_, TunnelMapObject(index)))
          val nextUnionFind = connectedRooms.foldLeft(roomConnections)(_.union(r1, _))
          nextMap -> nextUnionFind
        }
      }

    private def getPerimeter(i: Int): Seq[MapPoint] = {
      val room = rooms(i)
      (room.x.until(room.x + room.w).map(MapPoint(_, room.y - 1)) ++
          room.x.until(room.x + room.w).map(MapPoint(_, room.y + room.h)) ++
          room.y.until(room.y + room.h).map(MapPoint(room.x - 1, _)) ++
          room.y.until(room.y + room.h).map(MapPoint(room.x + room.w, _))
          ).filter(map.isInBounds)
    }
    private def keepMoving(mp: MapPoint): Boolean =
      map(mp).eq(FullWall) &&
          map.neighbors(mp).map(map.apply).flatMap(_.safeCast[RoomMapObject]).forall(_.index == r1)
  }
}

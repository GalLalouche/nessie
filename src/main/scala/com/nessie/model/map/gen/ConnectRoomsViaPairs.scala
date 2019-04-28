package com.nessie.model.map.gen

import com.nessie.common.collections.UnionFind
import common.rich.RichTuple._
import common.rich.collections.RichTraversableOnce._
import com.nessie.common.rng.Rngable
import com.nessie.common.rng.Rngable.ToRngableOps
import com.nessie.model.map.{BattleMap, FullWall, MapPoint}
import common.rich.RichT._
import common.rich.func.{MoreIterableInstances, ToMoreFoldableOps, ToMoreMonadPlusOps}

/**
 * Creates a BattleMap made up of just non-overlapping rooms. At the end of this procedure the resulting
 * [[BattleMap]] will be made up of [[FullWall]]s and [[RoomMapObject]]s.
 */
private object ConnectRoomsViaPairs
    extends ToMoreFoldableOps with ToMoreMonadPlusOps with MoreIterableInstances with ToRngableOps {
  def go(map: BattleMap): Rngable[BattleMap] = {
    new Aux(
      map = map,
      rooms = UnionFind(map.objects.view.map(_._2).select[RoomMapObject].map(_.index).toSet),
    ).finish
  }

  private class Aux(
      map: BattleMap,
      rooms: UnionFind[Int],
  ) {
    def finish: Rngable[BattleMap] = {
      if (rooms.numberOfSets == 1) Rngable.pure(map) else for {
        (r1, r2) <- rooms.values.unorderedPairs.toIterator.filterNot(_.reduce(rooms.sameSet)).sample
        (map, rooms) <- Connector(map, rooms, r1, r2).go
        result <- new Aux(map, rooms).finish
      } yield result
    }
  }
  private case class Connector(map: BattleMap, rooms: UnionFind[Int], r1: Int, r2: Int) {
    def go: Rngable[(BattleMap, UnionFind[Int])] =
      if (rooms.sameSet(r1, r2)) Rngable.pure(map -> rooms) else ???
  }
}

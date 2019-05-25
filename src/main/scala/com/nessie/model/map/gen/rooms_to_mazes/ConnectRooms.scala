package com.nessie.model.map.gen.rooms_to_mazes

import com.nessie.common.rng.Rngable
import com.nessie.model.map.{BattleMap, FullWall, MapPoint}
import common.rich.RichT._
import common.rich.func.{MoreIterableInstances, ToMoreFoldableOps, ToMoreMonadPlusOps}

/**
 * Creates a BattleMap made up of just non-overlapping rooms. At the end of this procedure the
 * resulting [[BattleMap]] will be made up of [[FullWall]]s and [[RoomMapObject]]s.
 */
private object ConnectRooms
    extends ToMoreFoldableOps with ToMoreMonadPlusOps with MoreIterableInstances {
  def go(map: BattleMap): Rngable[BattleMap] = {
    // TODO fix ToMoreMonadPlusOps.select to work with tuples
    Aux(
      map = map,
      remainingPoints = map.objects.filter(_._2.isInstanceOf[RoomMapObject]).map(_._1).toSet,
      Set.empty,
      List(MapPoint(0, 0)),
      0,
    ).dig
  }

  private case class Aux(
      map: BattleMap,
      remainingPoints: Set[MapPoint],
      visited: Set[MapPoint],
      path: List[MapPoint],
      index: Int
  ) {
    def dig: Rngable[BattleMap] = if (remainingPoints.isEmpty || path.isEmpty) Rngable.pure(map) else {
      val head :: tail = path
      if (visited(head))
        copy(path = tail).dig
      else Rngable.shuffle(map.neighbors(head).toIndexedSeq).map {shuffledNeighbors =>
        // TODO lenses?
        val dug = map(head) eq FullWall
        copy(
          map = map.place(head, if (dug) TunnelMapObject(index) else map(head)),
          remainingPoints = {
            val roomNumbers = map.neighbors(head).map(map.apply).select[RoomMapObject].map(_.index).toSet
            val $ = remainingPoints.filterNot(map(_).asInstanceOf[RoomMapObject].index |> roomNumbers)
            if ($ != remainingPoints)
              println(s"removed ${remainingPoints.&~($)} by point $head ($index)")
            $
          },
          visited = visited + head,
          path = shuffledNeighbors.toList ++ tail,
          index = index.mapIf(dug).to(_ + 1),
        )
      }.flatMap(_.dig)
    }
  }
}

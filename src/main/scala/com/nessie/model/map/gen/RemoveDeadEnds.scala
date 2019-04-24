package com.nessie.model.map.gen

import com.nessie.model.map.{BattleMap, MapPoint}
import common.rich.func.{MoreIterableInstances, ToMoreMonadPlusOps}

import scala.annotation.tailrec

/**
 * Remove all dead end-paths from the map. This is done iteratively by replacing all dead-end cells (i.e.,
 * cells with a single neighbor) with FullWalls and continuing until there are no dead-end cells left.
 */
private object RemoveDeadEnds extends ToMoreMonadPlusOps with MoreIterableInstances {
  // TODO implement more efficiently. Right now this is implemented in O(n^2), but it could be in O(n) by
  // TODO marking the neighbors of dead-end cells as the next to check.
  def apply(map: BattleMap): BattleMap = {
    @tailrec
    def aux(map: BattleMap): BattleMap = {
      val deadEnds: Iterable[MapPoint] = map.objects.view
          .collect {
            case e: (MapPoint, AlgorithmStepMapObject) => e._1
          }
          .filter(map.reachableNeighbors(_).size == 1)
      if (deadEnds.isEmpty) map else aux(deadEnds.foldLeft(map)((map, next) => map.remove(next).fill(next)))
    }
    assert(map.objects.forall(_._2.isInstanceOf[ReachableMapObject]))
    aux(map)
  }
}

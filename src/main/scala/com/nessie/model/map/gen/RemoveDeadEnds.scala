package com.nessie.model.map.gen

import com.nessie.model.map.{BattleMap, MapPoint}
import common.rich.func.{MoreIterableInstances, ToMoreMonadPlusOps}

import scala.annotation.tailrec

/**
 * Remove all dead end-paths from the map. This is done iteratively by replacing all dead-end cells (i.e.,
 * cells with a single neighbor) with FullWalls and continuing until there are no dead-end cells left.
 */
private object RemoveDeadEnds extends ToMoreMonadPlusOps with MoreIterableInstances {
  def apply(map: BattleMap): BattleMap = {
    @tailrec
    def aux(map: BattleMap, deadEnds: Iterable[MapPoint]): BattleMap = {
      if (deadEnds.isEmpty) map else {
        val nextMap = deadEnds.foldLeft(map)((map, next) => map.remove(next).fill(next))
        // After filling dead ends, the next potential dead ends must be a subset of the current dead-end
        // neighbors since they are only ones whose degree was changed.
        val nextDeadEnds =
          deadEnds.flatMap(map.reachableNeighbors).filter(e => hasExactSize(nextMap.reachableNeighbors(e), 1))
        aux(nextMap, nextDeadEnds)
      }
    }
    assert(map.objects.forall(_._2.isInstanceOf[ReachableMapObject]))
    aux(map, map.points.filter(e => hasExactSize(map.reachableNeighbors(e), 1)))
  }

  // TODO move to ScalaCommon
  private def hasExactSize(i: Iterable[_], size: Int) = {
    val iterator = i.iterator.drop(size - 1)
    iterator.hasNext && {iterator.next(); iterator.isEmpty}
  }
}

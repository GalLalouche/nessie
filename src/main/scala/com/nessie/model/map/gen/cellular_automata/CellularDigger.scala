package com.nessie.model.map.gen.cellular_automata

import com.nessie.common.graph.AStarTraversal
import com.nessie.common.rng.Rngable
import com.nessie.common.rng.Rngable.ToRngableOps
import com.nessie.model.map.BattleMap
import com.nessie.model.map.gen.MapGenerator
import common.rich.RichTuple._
import common.rich.primitives.RichBoolean._
import common.rich.RichT._
import common.rich.collections.RichTraversableOnce._

private class CellularDigger(private val map: BattleMap, caves: Caves) extends ToRngableOps {
  def next: Rngable[Option[CellularDigger]] = if (caves.isConnected) Rngable.pure(None) else {
    // TODO RTO.filterNot
    val pairs =
      caves.uf.values.unorderedPairs.filter(_.reduce(caves.areConnected(_, _).isFalse)).toVector.sorted
    for {
      (src, dst) <- pairs.sample
      start <- src.mapPoints.sample
      end <- dst.mapPoints.sample
      t <- AStarTraversal(map.toFullGraph, start, end)
      tunnelWidth <- PathWidener.Width.values.sample
    } yield {
      val path = t.takeWhile(caves.cave(_).forall(caves.areConnected(src, _))).toVector
      val widenedPath = PathWidener(path, tunnelWidth).toSet
      val clearedMap = map.foldPoints((map, p) => map
          .mapIf(widenedPath(p) && map(p).canMoveThrough.isFalse)
          .to(_.replaceSafely(p, Tunnel)))
      Some(new CellularDigger(clearedMap, Caves.from(clearedMap)))
    }
  }
}

private object CellularDigger {
  def generator(map: BattleMap): MapGenerator = {
    val caves = Caves.from(map)
    val markedMap = caves.mark(map)
    new MapGenerator {
      override def initialMap = markedMap
      override def iterativeGenerator =
        Rngable.iterateOptionally(new CellularDigger(markedMap, caves))(_.next).map(_.map(_.map))
      override def canonize(map: BattleMap) = AutomataGeneration.canonize(map)
    }
  }
}

package com.nessie.model.map.gen.cellular_automata

import com.nessie.common.graph.AStarTraversal
import com.nessie.common.rng.Rngable
import com.nessie.common.rng.Rngable.RngableOption
import com.nessie.common.rng.Rngable.ToRngableOps._
import com.nessie.model.map.BattleMap
import com.nessie.model.map.gen.MapIterator

import common.rich.RichT._
import common.rich.collections.RichTraversableOnce._
import common.rich.primitives.RichBoolean._

private class CellularDigger(private val map: BattleMap, caves: Caves) {
  def next: RngableOption[CellularDigger] =
    Rngable.unless(caves.isConnected) {
      val pairs =
        caves.uf.values.unorderedPairs.filterNot(Function tupled caves.areConnected).toVector.sorted
      for {
        (src, dst) <- pairs.sample
        start <- src.mapPoints.sample
        end <- dst.mapPoints.sample
        t <- AStarTraversal(map.toFullGraph, start, end)
            .takeWhile(caves.cave(_).forall(caves.areConnected(src, _)))
            .toStream
        tunnelWidth <- PathWidener.Width.values.sample
      } yield {
        val path = t.toVector
        val widenedPath = PathWidener(path, tunnelWidth).toSet
        val clearedMap = map.mapPoints((p, o) =>
          o.mapIf(widenedPath(p) && o.canMoveThrough.isFalse)
              .to(Tunnel)
        )
        new CellularDigger(clearedMap, Caves.from(clearedMap))
      }
    }
}

private object CellularDigger {
  def iterator(map: BattleMap): MapIterator = {
    val caves = Caves.from(map)
    val markedMap = caves.mark(map)
    new MapIterator {
      override def steps =
        Rngable.iterateOptionally(new CellularDigger(markedMap, caves))(_.next).map(_.map)
      override def canonize(map: BattleMap) = AutomataGeneration.canonize(map)
    }
  }
}

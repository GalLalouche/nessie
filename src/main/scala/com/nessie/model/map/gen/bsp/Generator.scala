package com.nessie.model.map.gen.bsp

import com.nessie.common.rng.Rngable
import com.nessie.common.rng.Rngable.RngableIterable
import com.nessie.model.map.{BattleMap, GridSize}
import com.nessie.model.map.gen.{MapIterator, MapIteratorFactory}

private class Generator(gs: GridSize) extends MapIterator {
  def partitions: RngableIterable[MapPartitioning] =
    Rngable.iterateOptionally(MapPartitioning(gs))(_.split)
  override def steps = partitions.map(_.map(_.toMap))
  override def canonize(map: BattleMap) = ???
}
private object Generator extends MapIteratorFactory {
  override def generate(gs: GridSize) = new Generator(gs)
}

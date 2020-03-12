package com.nessie.model.map.gen.bsp

import com.nessie.common.rng.Rngable
import com.nessie.model.map.{BattleMap, GridSize}
import com.nessie.model.map.gen.{MapIterator, MapIteratorFactory}

import common.rich.collections.LazyIterable

private class Generator(gs: GridSize) extends MapIterator {
  def partitions: Rngable[LazyIterable[MapPartitioning]] =
    Rngable.iterateOptionally(MapPartitioning(gs))(_.split)
  override def steps = partitions.map(_.map(_.toMap))
  override def canonize(map: BattleMap) = ???
}
private object Generator extends MapIteratorFactory {
  override def generate(gs: GridSize) = new Generator(gs)
}

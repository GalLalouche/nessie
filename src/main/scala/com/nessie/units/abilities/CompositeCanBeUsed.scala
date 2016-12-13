package com.nessie.units.abilities
import com.nessie.map.model.{BattleMap, MapPoint}

private class CompositeCanBeUsed(val canBeUsed: Seq[CanBeUsed]) extends CanBeUsed {
  override def apply(battleMap: BattleMap, source: MapPoint, destination: MapPoint): Boolean =
    canBeUsed.forall(_(battleMap, source, destination))
}

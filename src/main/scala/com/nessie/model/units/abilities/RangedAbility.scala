package com.nessie.model.units.abilities

import com.nessie.model.map.{BattleMap, MapPoint}
import com.nessie.common.graph.RichUndirected._

trait RangedAbility extends UnitAbility {
  def range: Int
  override def canBeApplied(m: BattleMap, source: MapPoint) =
    m.toGraph.distances(source).mapValues(_ <= range)
}

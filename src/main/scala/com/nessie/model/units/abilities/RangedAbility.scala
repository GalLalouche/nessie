package com.nessie.model.units.abilities

import com.nessie.common.graph.RichGraphs._
import com.nessie.model.map.{BattleMap, MapPoint}

trait RangedAbility extends UnitAbility {
  def range: Int
  override def canBeApplied(m: BattleMap, source: MapPoint) =
    m.toGraph.distances(source).mapValues(_ <= range)
}

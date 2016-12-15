package com.nessie.model.units.abilities

import com.nessie.model.map.{BattleMap, MapPoint}

private trait CanBeUsed {
  def apply(battleMap: BattleMap, source: MapPoint, destination: MapPoint): Boolean
}

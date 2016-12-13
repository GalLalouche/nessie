package com.nessie.units.abilities

import com.nessie.map.model.{BattleMap, MapPoint}

private trait CanBeUsed {
  def apply(battleMap: BattleMap, source: MapPoint, destination: MapPoint): Boolean
}

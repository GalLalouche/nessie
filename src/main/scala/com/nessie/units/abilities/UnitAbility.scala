package com.nessie.units.abilities

import com.nessie.gm.GameState
import com.nessie.map.model.{BattleMap, MapPoint}
import common.rich.RichT._

trait UnitAbility {
  private lazy val constraints: CanBeUsed = CanBeUsed extract this
  def name: String = this.simpleName
  def applyTo(source: MapPoint, destination: MapPoint): GameState => GameState
  def canBeUsed(battleMap: BattleMap, source: MapPoint, destination: MapPoint): Boolean =
    constraints(battleMap, source, destination)
}

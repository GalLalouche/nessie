package com.nessie.model.units.abilities

import com.nessie.gm.GameState
import com.nessie.model.map.{BattleMap, MapPoint}
import common.rich.RichT._

import scala.collection.mutable.ListBuffer

trait UnitAbility {
  private val constraints = new ListBuffer[CanBeUsed]
  private[abilities] def addConstraint(canBeUsed: CanBeUsed) = constraints += canBeUsed
  def name: String = this.simpleName
  def applyTo(source: MapPoint, destination: MapPoint): GameState => GameState
  def canBeUsed(battleMap: BattleMap, source: MapPoint, destination: MapPoint): Boolean =
    constraints.forall(_(battleMap, source, destination))
}

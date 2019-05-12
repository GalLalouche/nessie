package com.nessie.gm

import com.nessie.model.map.MapPoint
import com.nessie.model.units.CombatUnit
import com.nessie.model.units.abilities.MoveAbility
import monocle.macros.Lenses

sealed trait ComposedTurn {
  def unit: CombatUnit
  def append(m: Movement): ComposedTurn
  def +(m: Movement): ComposedTurn = append(m)
  // TODO specify order
  def movements: Seq[Movement]
  def totalMovement: Int = movements.view.map(_.manhattanDistance).sum
  def remainingMovement: Int = unit.moveAbility.range - totalMovement
  require(remainingMovement >= 0)
  def remainingMovementAbility: MoveAbility = MoveAbility(remainingMovement)
  def canAppendAction: Boolean
}

sealed trait UnitAction {
  def turnDelay: Double
}
case class Attack(
    src: MapPoint, dst: MapPoint, damageAmount: Int, override val turnDelay: Double) extends UnitAction
case class Movement(src: MapPoint, dst: MapPoint) {
  def manhattanDistance: Int = src manhattanDistanceTo dst
}
@Lenses
case class PreAction(unit: CombatUnit, movements: List[Movement]) extends ComposedTurn {
  require(remainingMovement >= 0)
  override def append(movement: Movement): PreAction = PreAction.movements.modify(movement :: _)(this)
  def append(attack: Attack): PostAction = PostAction(unit, movements, attack, Nil)
  def +(attack: Attack): PostAction = append(attack)
  override def canAppendAction: Boolean = true
}
object PreAction {
  def empty(unit: CombatUnit) = PreAction(unit, Nil)
}
case class PostAction(
    unit: CombatUnit,
    preActionMovements: List[Movement],
    action: Attack,
    postActionMovements: List[Movement],
) extends ComposedTurn {
  override def append(movement: Movement): PostAction =
    copy(postActionMovements = movement :: postActionMovements)
  override def movements: Seq[Movement] = postActionMovements ++ preActionMovements
  override def canAppendAction: Boolean = false
}

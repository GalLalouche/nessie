package com.nessie.model.units.abilities

import com.nessie.common.graph.RichUndirected._
import com.nessie.model.map.{BattleMap, CombatUnitObject, MapPoint}
import common.rich.primitives.RichBoolean._
import common.rich.RichT._

object CanBeUsed {
  private type Constraint = (BattleMap, MapPoint, MapPoint) => Boolean
  private def composite(c1: Constraint, cn: Constraint*): Constraint = new Constraint {
    val all = c1 +: cn
    override def apply(map: BattleMap, src: MapPoint, dst: MapPoint) = all.forall(_ (map, src, dst))
  }
  private def inRange(range: Int): Constraint = (map, src, dst) =>
    map.toPointGraph.distance(src, dst).exists(d => d > 0 && d <= range)
  private def emptyDst: Constraint = (map, _, dst) => map.isEmptyAt(dst)
  private def differentOwner: Constraint = (map, src, dst) => {
    def getOwner(point: MapPoint) = map(point).safeCast[CombatUnitObject].map(_.unit.owner)
    (for {
      sOwner <- getOwner(src)
      dOwner <- getOwner(dst)
    } yield sOwner != dOwner) getOrElse false
  }
  // Ideally a would be anonymous, but then the object CanBeUsed can't be used as a pure function
  def apply(a: UnitAbility): (BattleMap, MapPoint, MapPoint) => Boolean = a match {
    case MoveAbility(range) => composite(inRange(range), emptyDst)
    case d: DamageAbility =>
      val range = d match {
        case MeleeAttack(_) => 1
        case RangedAttack(_, r) => r
      }
      composite(differentOwner, inRange(range))
  }
  def getUsablePoints(a: UnitAbility)(map: BattleMap, source: MapPoint): Iterable[MapPoint] =
    map.points.filter(apply(a)(map, source, _))
  def negate: UnitAbility => (BattleMap, MapPoint, MapPoint) => Boolean = u => apply(u)(_, _, _).isFalse
}

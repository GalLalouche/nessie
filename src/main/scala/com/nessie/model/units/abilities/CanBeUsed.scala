package com.nessie.model.units.abilities

import com.nessie.common.graph.GridDijkstra
import com.nessie.common.graph.GridDijkstra.Blockable
import com.nessie.model.map.{BattleMap, BattleMapObject, CombatUnitObject, MapPoint}
import common.rich.collections.RichTraversableOnce._
import common.rich.RichT._
import common.rich.primitives.RichBoolean._

object CanBeUsed {
  private type Constraint = (BattleMap, MapPoint, MapPoint) => Boolean
  private def composite(c1: Constraint, cn: Constraint*): Constraint = new Constraint {
    val all = c1 +: cn
    override def apply(map: BattleMap, src: MapPoint, dst: MapPoint) = all.forall(_ (map, src, dst))
  }
  private def inRange(range: Int): Constraint = (map, src, dst) =>
    if (range == 1) src.manhattanDistanceTo(dst) <= 1 && map(dst).canMoveThrough && map(src).canMoveThrough
    // TODO use the algorithm variant for a specific target.
    else allInRange(map, src)(range).contains(dst)
  private implicit val blockableEv: Blockable[BattleMapObject] = Blockable(_.canMoveThrough.isFalse)
  private def allInRange(map: BattleMap, src: MapPoint)(range: Int): Iterable[MapPoint] =
    GridDijkstra(map.grid, src, range).keys.toVector
  private def emptyDst: Constraint = (map, _, dst) => map.isEmptyAt(dst)
  private def differentOwner: Constraint = (map, src, dst) => {
    def getOwner(point: MapPoint) = map(point).safeCast[CombatUnitObject].map(_.unit.owner)
    (for {
      sOwner <- getOwner(src)
      dOwner <- getOwner(dst)
    } yield sOwner != dOwner) getOrElse false
  }
  // Ideally a would be anonymous, but then the object CanBeUsed can't be used as a pure function
  // TODO Right now ranged attacks use BFS, which should only be applied to movement.
  def apply(a: UnitAbility): (BattleMap, MapPoint, MapPoint) => Boolean = a match {
    case MoveAbility(range) => composite(inRange(range), emptyDst)
    case d: DamageAbility =>
      val range = d match {
        case MeleeAttack(_) => 1
        case RangedAttack(_, r) => r
      }
      composite(differentOwner, inRange(range))
  }
  def getUsablePoints(a: UnitAbility)(map: BattleMap, source: MapPoint): Iterable[MapPoint] = {
    val inRange = allInRange(map, source) _
    a match {
      case MoveAbility(range) => inRange(range).filter(map.isEmptyAt)
      case d: DamageAbility => inRange(d match {
        case MeleeAttack(_) => 1
        case RangedAttack(_, r) => r
      }).filter(differentOwner(map, source, _))
    }
  }
  def negate: UnitAbility => (BattleMap, MapPoint, MapPoint) => Boolean = u => apply(u)(_, _, _).isFalse
}

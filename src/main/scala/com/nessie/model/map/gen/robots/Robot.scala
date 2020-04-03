package com.nessie.model.map.gen.robots

import com.nessie.common.rng.Rngable
import com.nessie.common.rng.Rngable.RngableOption
import com.nessie.model.map.BattleMap

import scala.language.higherKinds

import scalaz.Monoid
import common.rich.func.TuplePLenses

/** A robot can perform some action to modify the [[BattleMap]] and then spawn another child robot. */
private trait Robot {
  def go(map: BattleMap): RngableOption[(BattleMap, Robot)]
}

private object Robot {
  /** A robot that does nothing */
  case object EmptyRobot extends Robot {
    override def go(map: BattleMap): RngableOption[(BattleMap, Robot)] = Rngable.none
  }
  /** Creates a single use robot, i.e., one that does not spawn other robots after finishing. */
  def apply(f: BattleMap => BattleMap): Robot = {map: BattleMap => Rngable.some(f(map) -> EmptyRobot)}
  /**
   * The main way to compose robots.
   * Performs a depth first application of all the robots originating from r1, then does the same for
   * remainder robots.
   */
  def dfsComposite(r1: Robot, rs: Robot*): Robot = new DFSCompositeRobot(r1 :: rs.toList)
  private class DFSCompositeRobot(robots: List[Robot]) extends Robot {
    override def go(map: BattleMap) = robots match {
      case Nil => Rngable.none
      case h :: tl =>
        val appended: RngableOption[(BattleMap, Robot)] = h.go(map)
            .map(TuplePLenses.tuple2Second.modify(r => new DFSCompositeRobot(r :: tl)))
        appended ||| new DFSCompositeRobot(tl).go(map)
    }
  }
  implicit object MonoidEv extends Monoid[Robot] {
    override def zero: Robot = EmptyRobot
    override def append(f1: Robot, f2: => Robot): Robot = dfsComposite(f1, f2)
  }
}
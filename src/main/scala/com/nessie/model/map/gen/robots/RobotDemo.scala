package com.nessie.model.map.gen.robots

import com.nessie.common.rng.{Rngable, StdGen}
import com.nessie.common.rng.Rngable.ToRngableOps._
import com.nessie.model.map.{BattleMap, EmptyMapObject, VectorGrid}
import com.nessie.model.map.gen.DemoImageViewer

import common.rich.RichT._
import common.rich.RichTuple._

private object RobotDemo extends DemoImageViewer {
  override protected def mapFunction = {
    val iterator = StdGen(0).iterator

    val side = 50
    val map = BattleMap.create(VectorGrid, side, side)
    val generator: Robot = Tunneler(map).mkRandom(iterator.next())
    lazy val base = Rngable
        .iterateOptionally((generator, map))(_.reduce(_ go _).map(_.swap))
        .map(_._2)
        .mkRandom(iterator.next())

    {
      case 0 => base.map(BattleMapImageDrawer.apply)
      case 1 => Stream(BattleMapImageDrawer(base.last.map(_.mapIf(EmptyMapObject != _).to(Room(5)))))
    }
  }
}

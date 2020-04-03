package com.nessie.model.map.gen.robots

import com.nessie.common.rng.Rngable
import com.nessie.common.rng.Rngable.ToRngableOps._
import com.nessie.model.map.{BattleMap, Direction, MapPoint}

/** A junction is basically a room around a path. */
private case class Junctioner(center: MapPoint, radius: Int, generation: Int) extends Robot {
  override def go(map: BattleMap) = {
    val points: Iterable[MapPoint] = center.squareRadius(radius)
    Rngable.when(points.forall(map.isInBounds)) {
      // A Junction can continue from one of its sides with a new tunnel.
      val nextRobot = Rngable.withProbability[Robot](0.8) {
        for {
          direction <- mkRandom[Direction]
          length <- Tunneler.TunnelLength
        } yield new Tunneler(
          startingPosition = center.go(direction, radius + 1),
          direction = direction,
          tunnelWidth = 1,
          tunnelLength = length,
          generation = generation + 1,
        )
      }.orZero
      val nextMap = points.foldLeft(map)(_.place(_, Junction(generation)))
      nextRobot.map(nextMap.->)
    }
  }
}

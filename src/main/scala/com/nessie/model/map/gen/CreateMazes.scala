package com.nessie.model.map.gen

import com.nessie.common.rng.Rngable
import com.nessie.common.rng.Rngable.ToRngableOps
import com.nessie.model.map.{BattleMap, DirectionalMapPoint, EmptyMapObject, FullWall, MapPoint}
import common.rich.RichT._
import common.rich.func.ToMoreFoldableOps

import scalaz.std.OptionInstances
import scalaz.syntax.ToMonadOps

/** Fills up the locations between the rooms. */
private object CreateMazes extends ToMoreFoldableOps with OptionInstances {
  def go(map: BattleMap): Rngable[BattleMap] = new Aux(nonEmptyPoints(map), map).finish

  private class Aux(remainingCells: Set[MapPoint], result: BattleMap)
      extends ToMonadOps with ToRngableOps with ToMoreFoldableOps with OptionInstances {
    private def dig(mp: MapPoint): Rngable[Aux] = new Digger(result, List(mp), 0).dig
        .map(newMap => new Aux(nonEmptyPoints(newMap), newMap))
    def finish: Rngable[BattleMap] =
      remainingCells.headOption.mapHeadOrElse(dig(_).flatMap(_.finish), Rngable.pure(result))
  }

  private class Digger(map: BattleMap, path: List[MapPoint], index: Int) {
    def dig: Rngable[BattleMap] = path match {
      case Nil => Rngable.pure(map)
      case mp :: tail =>
        val mapDugAtLocation = map.mapIf(_ (mp) == FullWall).to(_.replace(mp, TunnelMapObject(index)))
        val filledNeighbors = map.neighbors(mp).filterNot(isEmptyAt(mapDugAtLocation, _)).toVector
        if (filledNeighbors.isEmpty) new Digger(mapDugAtLocation, tail, index).dig else for {
          next <- Rngable.sample(filledNeighbors)
          wall = DirectionalMapPoint.between(mp, next)
          mapWithoutWall = mapDugAtLocation.remove(wall)
          result <- new Digger(mapWithoutWall, next :: path, index + 1).dig
        } yield result
    }
  }

  private def nonEmptyPoints(map: BattleMap): Set[MapPoint] =
    map.points.map(_._1).filterNot(isEmptyAt(map, _)).toSet
  private def isEmptyAt(map: BattleMap, p: MapPoint): Boolean = map(p) match {
    case EmptyMapObject => true
    case TunnelMapObject(_) => true
    case RoomMapObject(_) => true
    case _ => false
  }
}

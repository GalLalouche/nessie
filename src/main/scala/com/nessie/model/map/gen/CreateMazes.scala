package com.nessie.model.map.gen

import com.nessie.common.rng.Rngable
import com.nessie.common.rng.Rngable.ToRngableOps
import com.nessie.model.map.{BattleMap, DirectionalMapPoint, EmptyMapObject, FullWall, MapPoint, TunnelMapObject}
import common.rich.RichT._
import common.rich.func.ToMoreFoldableOps

import scalaz.std.OptionInstances
import scalaz.syntax.ToMonadOps

/** Fills up the locations between the rooms. */
private class CreateMazes(
    remainingCells: Set[MapPoint], private val result: BattleMap
) extends ToMonadOps with ToRngableOps
    with ToMoreFoldableOps with OptionInstances {
  import com.nessie.model.map.gen.CreateMazes._

  private def dig(mp: MapPoint): Rngable[CreateMazes] = new Digger(result, List(mp), 0).dig
      .map(newMap => new CreateMazes(nonEmptyPoints(newMap), newMap))
  def finish: Rngable[BattleMap] =
    remainingCells.headOption.mapHeadOrElse(dig(_).flatMap(_.finish), Rngable.pure(result))
}

private object CreateMazes extends ToMoreFoldableOps with OptionInstances {
  private def nonEmptyPoints(map: BattleMap): Set[MapPoint] =
    map.points.map(_._1).filterNot(isEmptyAt(map, _)).toSet
  private def isEmptyAt(map: BattleMap, p: MapPoint): Boolean = map(p) match {
    case EmptyMapObject => true
    case TunnelMapObject(_) => true
    case _ => false
  }
  def go(rooms: MapWithRooms): Rngable[MapWithRooms] =
    new CreateMazes(nonEmptyPoints(rooms.map), rooms.map)
        .finish
        .map(MapWithRooms(_, rooms.roomPoints))

  private class Digger(map: BattleMap, path: List[MapPoint], index: Int) {
    def dig: Rngable[BattleMap] = path match {
      case Nil => Rngable.pure(map)
      case mp :: tail =>
        println(s"At <$mp>")
        val mapDiggedAtLocation = map.mapIf(_ (mp) == FullWall).to(_.remove(mp).place(mp, TunnelMapObject(index)))
        val filledNeighbors = map.neighbors(mp).filterNot(isEmptyAt(mapDiggedAtLocation, _)).toVector
        if (filledNeighbors.isEmpty) {
          println("No filled neighbors, retreating")
          new Digger(mapDiggedAtLocation, tail, index).dig
        } else {
          for {
            next <- Rngable.sample(filledNeighbors)
            _ = println(s"Next = <$next>")
            wall = DirectionalMapPoint.between(mp, next)
            _ = println(s"Tearing down the wall @ <$wall>")
            mapWithoutWall = mapDiggedAtLocation.remove(wall)
            _ = println(s"Moving on to <$next>")
            result <- new Digger(mapWithoutWall, next :: path, index + 1).dig
          } yield result
        }
    }
  }
}

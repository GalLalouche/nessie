package com.nessie.model.map.gen.cellular_automata

import com.nessie.common.rng.Rngable
import com.nessie.model.map.{BattleMap, FullWall, MapPoint, VectorBattleMap}
import com.nessie.model.map.gen.MapGenerator
import common.rich.primitives.RichBoolean._
import common.rich.RichT._
import common.Percentage
import common.rich.collections.LazyIterable

private object CellularAutomataGenerator extends MapGenerator {
  override def generator: Rngable[BattleMap] = iterativeGenerator.map(_.last)

  override def iterativeGenerator: Rngable[LazyIterable[BattleMap]] = {
    val Width = 50
    val Height = 50
    val initialProbability: Percentage = 0.52
    val battleMap = VectorBattleMap(Width, Height)

    for {
      initialMap <- Rngable.fromRandom(random => {
        battleMap.foldPoints {(map, mp) =>
          val next = if (initialProbability.roll(random)) Empty(0) else Wall(0)
          map.place(mp, next)
        }
      })
    } yield {
      val iterator = new MapIterator(initialMap, 1)
      // TODO iterateOptionally in LazyIterable
      LazyIterable.iterate(Option(iterator))(_.get.next).takeWhile(_.isDefined).map(_.get.map)
    }
  }

  private class MapIterator(val map: BattleMap, n: Int) {
    def wallsInDistance(mp: MapPoint, n: Int): Int = (
        for {
          x <- mp.x - n to mp.x + n
          y <- mp.y - n to mp.y + n
        } yield MapPoint(x, y)
        ).count(mp => map.isInBounds(mp).isFalse || map(mp).isInstanceOf[Wall])

    def next: Option[MapIterator] = {
      val nextMap: BattleMap = map.foldPoints((map, mp) => {
        val wasWall = map(mp).isInstanceOf[Wall]
        val r1 = wallsInDistance(mp, 1)
        val r2 = wallsInDistance(mp, 2)
        val isWall = r1 >= 5
        val changed = isWall != wasWall
        if (changed.isFalse) map else map.replace(mp, if (isWall) Wall(n) else Empty(n))
      })

      nextMap.opt
          .filter(_.objects.toVector != map.objects.toVector)
          .map(new MapIterator(_, n + 1))
    }
  }

  override def canonize(currentMap: BattleMap): BattleMap =
    currentMap.foldPoints((map, next) => map(next) match {
      case Empty(_) => map.remove(next)
      case Wall(_) => map.replace(next, FullWall)
    })
}
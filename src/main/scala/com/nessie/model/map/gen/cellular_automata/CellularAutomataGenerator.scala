package com.nessie.model.map.gen.cellular_automata

import com.nessie.common.rng.Rngable
import com.nessie.model.map.{BattleMap, GridSize, MapPoint, VectorGrid}
import com.nessie.model.map.gen.{MapIterator, MapIteratorFactory}
import common.Percentage
import common.rich.RichT._
import common.rich.collections.LazyIterable
import common.rich.primitives.RichBoolean._

private class CellularAutomataGenerator private(gs: GridSize) extends MapIterator {
  override def steps: Rngable[LazyIterable[BattleMap]] = {
    val initialProbability: Percentage = 0.52
    val initialMap = BattleMap.create(VectorGrid, gs)
    for {
      initialMap <- Rngable.fromRandom(random => {
        initialMap.foldPoints {(map, mp) =>
          val next = if (initialProbability.roll(random)) Empty(0) else Wall(0)
          map.place(mp, next)
        }
      })
    } yield LazyIterable.iterateOptionally(new Aux(initialMap, 1))(_.next).map(_.map)
  }

  private class Aux(val map: BattleMap, n: Int) {
    private def wallsInDistance(mp: MapPoint, n: Int): Int = (
        for {
          x <- mp.x - n to mp.x + n
          y <- mp.y - n to mp.y + n
        } yield MapPoint(x, y)
        ).count(mp => map.isInBounds(mp).isFalse || map(mp).isInstanceOf[Wall])

    def next: Option[Aux] = {
      val nextMap: BattleMap = map.foldPoints((map, mp) => {
        val wasWall = map(mp).isInstanceOf[Wall]
        val r1 = wallsInDistance(mp, 1)
        val isWall = r1 >= 5
        val changed = isWall != wasWall
        if (changed.isFalse) map else map.place(mp, if (isWall) Wall(n) else Empty(n))
      })

      nextMap.opt
          .filter(_.objects.toVector != map.objects.toVector)
          .map(new Aux(_, n + 1))
    }
  }
  override def canonize(map: BattleMap) = AutomataGeneration.canonize(map)
}
private object CellularAutomataGenerator extends MapIteratorFactory {
  override def generate(gs: GridSize) = new CellularAutomataGenerator(gs)
}

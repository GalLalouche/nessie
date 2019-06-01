package com.nessie.model.map.gen.mob

import com.nessie.common.rng.Rngable
import com.nessie.common.rng.Rngable.ToRngableOps
import com.nessie.model.map.{BattleMap, CombatUnitObject, MapPoint}
import com.nessie.model.map.gen.MapIterator
import com.nessie.model.units.{Warrior, Zombie}
import common.rich.collections.LazyIterable

object DemoMobPlacer extends MobPlacer with ToRngableOps {
  // Step 1: pick a nice open area to place the player
  // Step 2: pick another nicer open area, far enough from the first, to place monsters
  // Step 3: Profit
  override def place(map: BattleMap): Rngable[BattleMap] = {
    def isOpen(n: Int)(mp: MapPoint): Boolean = (for {
      x <- mp.x - n to mp.x + n
      y <- mp.y - n to mp.y + n
    } yield MapPoint(x, y))
        .view.forall(p => map.isInBounds(p) && map.isEmptyAt(p))

    for {
      playerSpot <- map.points.filter(isOpen(2)).toVector.sample
      enemySpot <- map.points.view.filter(_.manhattanDistanceTo(playerSpot) >= 10).filter(isOpen(2)).sample
    } yield map
        .place(playerSpot, CombatUnitObject(Warrior.create))
        .place(enemySpot, CombatUnitObject(Zombie.create))
  }
  override def iterator(map: BattleMap): MapIterator = new MapIterator {
    // TODO LazyIterable.apply
    override def steps = place(map).map(Iterator(_)).map(LazyIterable.from(_))
    override def canonize(map: BattleMap) = map
  }
}

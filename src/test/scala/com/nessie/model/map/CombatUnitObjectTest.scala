package com.nessie.model.map

import com.nessie.model.units.{Archer, Warrior}
import common.test.AuxSpecs
import org.scalatest.FreeSpec

class CombatUnitObjectTest extends FreeSpec with AuxSpecs {
  private val exists = Archer.create
  private val missing = Warrior.create
  private val map = BattleMap.create(VectorGrid, 2, 2).place(MapPoint(1, 0), CombatUnitObject(exists))
  "findIn" - {
    "exists" in {
      CombatUnitObject.findIn(exists, map).get shouldReturn MapPoint(1, 0)
    }
    "missing" in {
      CombatUnitObject.findIn(missing, map) shouldReturn None
    }
  }

  "lens" - {
    "exists" in {
      val lens = CombatUnitObject.lens(exists)
      lens.get(map).get shouldReturn CombatUnitObject(exists)
      lens.set(None)(map).apply(MapPoint(1, 0)) shouldReturn EmptyMapObject
      val unitObject = CombatUnitObject(missing)
      lens.set(Some(unitObject))(map).apply(MapPoint(1, 0)) shouldReturn unitObject
    }
    "missing" in {
      val lens = CombatUnitObject.lens(missing)
      lens.get(map) shouldReturn None
      // set is no-op, since it's a lens to None
      val updated = lens.set(Some(CombatUnitObject(missing)))(map)
      lens.get(updated) shouldReturn None
    }
  }
}

package com.nessie.model.map

import com.nessie.model.units.Warrior
import org.scalatest.FreeSpec

import common.test.AuxSpecs

class BattleMapPrinterTest extends FreeSpec with AuxSpecs {
  "test" in {
    BattleMapPrinter(
      BattleMap.create(VectorGrid, 2, 3)
          .place(MapPoint(0, 1), FullWall)
          .place(MapPoint(1, 2), CombatUnitObject(Warrior.create))
    ) shouldReturn
        """_,_
          |*,_
          |_,W""".stripMargin
  }
}

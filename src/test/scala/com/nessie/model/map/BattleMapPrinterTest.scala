package com.nessie.model.map

import com.nessie.model.units.Warrior
import common.AuxSpecs
import org.scalatest.FreeSpec

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

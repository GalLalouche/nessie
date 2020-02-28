package com.nessie.model.units

import common.test.AuxSpecs
import org.scalatest.FreeSpec

class PlayerUnitTest extends FreeSpec with AuxSpecs {
  "Keep abilities after hit point change" - {
    "playerUnit" in {
      val $ = Archer.create
      $.hitPointsLens.modify(_.reduceHp(1))($).abilities shouldReturn $.abilities
    }
    "monster" in {
      val $ = Zombie.create
      $.hitPointsLens.modify(_.reduceHp(1))($).abilities shouldReturn $.abilities
    }
  }
}

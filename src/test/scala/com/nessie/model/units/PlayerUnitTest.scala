package com.nessie.model.units

import org.scalatest.FreeSpec

import common.test.AuxSpecs

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

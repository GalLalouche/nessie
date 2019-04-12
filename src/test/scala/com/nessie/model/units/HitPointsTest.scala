package com.nessie.model.units

import common.AuxSpecs
import org.scalatest.FreeSpec

class HitPointsTest extends FreeSpec with AuxSpecs {
  private val $ = HitPoints(currentHp = 50, maxHp = 100)

  "ctor" - {
    "throws if currentHp < 0" in {
      an[IllegalArgumentException] shouldBe thrownBy {HitPoints(currentHp = -1, maxHp = 100)}
    }
    "throws if currentHp > maxHp" in {
      an[IllegalArgumentException] shouldBe thrownBy {HitPoints(currentHp = 101, maxHp = 100)}
    }
  }
  "reduceHp" - {
    "throws on negative" in {
      an[IllegalArgumentException] shouldBe thrownBy {$.reduceHp(-1)}
    }
    "reduces HP" in {
      $.reduceHp(10) shouldReturn HitPoints(currentHp = 40, maxHp = 100)
    }
    "Doesn't reduce hp to more than 0" in {
      $.reduceHp(60) shouldReturn HitPoints(currentHp = 0, maxHp = 100)
    }
  }
  "healHp" - {
    "throws on negative" in {
      an[IllegalArgumentException] shouldBe thrownBy {$.healHp(-1)}
    }
    "heals HP" in {
      $.healHp(10) shouldReturn HitPoints(currentHp = 60, maxHp = 100)
    }
    "Doesn't heal hp to more than 0" in {
      $.healHp(60) shouldReturn HitPoints(currentHp = 100, maxHp = 100)
    }
  }
}

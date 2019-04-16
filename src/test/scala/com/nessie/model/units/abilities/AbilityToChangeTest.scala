package com.nessie.model.units.abilities

import com.nessie.gm.{Attack, Movement}
import com.nessie.model.map.MapPoint
import common.AuxSpecs
import org.scalatest.FreeSpec

class AbilityToChangeTest extends FreeSpec with AuxSpecs {
  private val src = MapPoint(0, 0)
  private val dst = MapPoint(1, 1)
  "MoveAbility" in {
    AbilityToChange(MoveAbility(3))(src, dst) shouldReturn Movement(src, dst)
  }
  "Damage ability" in {
    AbilityToChange(MeleeAttack(3))(src, dst) shouldReturn Attack(src, dst, 3)
  }
}

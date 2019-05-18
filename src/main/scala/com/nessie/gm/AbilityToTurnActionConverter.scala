package com.nessie.gm

import com.nessie.model.map.MapPoint
import com.nessie.model.units.abilities.UnitAbility

trait AbilityToTurnActionConverter {
  def apply(ability: UnitAbility)(src: MapPoint, dst: MapPoint): TurnAction
}

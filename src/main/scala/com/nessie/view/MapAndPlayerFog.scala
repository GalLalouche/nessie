package com.nessie.view

import com.nessie.model.map.fov.FogOfWar
import com.nessie.model.map.BattleMap

private case class MapAndPlayerFog(map: BattleMap, fogOfWar: FogOfWar) {
  require(map.grid.size == fogOfWar.grid.size)
}

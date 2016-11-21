package com.nessie.view.map

import com.nessie.map.model.{BattleMap, BattleMapController}

trait MapViewFactory extends ((BattleMap, BattleMapController) => MapView) {
}

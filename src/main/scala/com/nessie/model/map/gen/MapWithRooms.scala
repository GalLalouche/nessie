package com.nessie.model.map.gen

import com.nessie.model.map.{BattleMap, MapPoint}

private case class MapWithRooms(map: BattleMap, roomPoints: Set[MapPoint])

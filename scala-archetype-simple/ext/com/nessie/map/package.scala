package com.nessie

import com.nessie.map.model.{BattleMapModifier, BattleMap, MapPoint}

package object map {
	implicit def tupleToMapPoint(p: (Int, Int)) = {
		require(p != null)
		new MapPoint(p._1, p._2)
	}

	implicit def mapToModifier(m: BattleMap) = {
		require(m != null)
		BattleMapModifier(m)
	}
}
package com.nessie.model

package object map {
	implicit def tupleToMapPoint(p: (Int, Int)) = {
		require(p != null)
		new MapPoint(p._1, p._2)
	}
}
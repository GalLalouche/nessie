package com.nessie.model

package object map {
	class MapPoint(val x: Int, val y: Int) {
		override def toString() = (x, y).toString
	}
}
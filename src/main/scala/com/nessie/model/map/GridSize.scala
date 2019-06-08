package com.nessie.model.map

case class GridSize(width: Int, height: Int) {
  def isInBounds(mp: MapPoint): Boolean = mp.x >= 0 && mp.y >= 0 && mp.x < width && mp.y < height
}

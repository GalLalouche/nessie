package com.nessie.view.zirconview.map

import com.nessie.model.map.{GridSize, MapPoint}
import org.hexworks.zircon.api.data.Size

private trait ScrollableMapViewProperties {
  def getCurrentMapSize: GridSize
  def getCurrentOffset: MapPoint
  val graphicsSize: Size
}

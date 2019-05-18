package com.nessie.view.zirconview

import com.nessie.model.map.MapPoint
import org.hexworks.zircon.api.data.Position

private trait MapPointConverter {
  def toAbsolutePosition(mp: MapPoint): Position
  def fromAbsolutePosition(p: Position): Option[MapPoint]
}

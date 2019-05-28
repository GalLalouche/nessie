package com.nessie.view.zirconview.map

import com.nessie.model.map.MapPoint
import org.hexworks.zircon.api.data.Position

private[zirconview] trait MapPointConverter {
  def toAbsolutePosition(mp: MapPoint): Position
  def fromAbsolutePosition(p: Position): Option[MapPoint]
}

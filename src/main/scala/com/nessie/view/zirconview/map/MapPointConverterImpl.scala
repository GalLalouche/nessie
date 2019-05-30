package com.nessie.view.zirconview.map

import com.nessie.model.map.MapPoint
import com.nessie.view.zirconview.ZirconUtils._
import org.hexworks.zircon.api.data.Position

private class MapPointConverterImpl(
    properties: ScrollableMapViewProperties,
    absolutePosition: Position,
) extends MapPointConverter {
  private val graphicsSize = properties.graphicsSize
  override def toAbsolutePosition(mp: MapPoint) =
    toRelativePosition(mp).map(_.withRelative(absolutePosition))
  override def toRelativePosition(mp: MapPoint) =
    if (isInBounds(mp)) {
      Some(mp.toPosition.withInverseRelative(properties.getCurrentOffset.toPosition))
    } else None
  override def fromAbsolutePosition(p: Position) =
    if (isAbsolutePositionInBounds(p)) {
      val $ = p.withInverseRelative(absolutePosition).withRelative(properties.getCurrentOffset.toPosition)
      Some(MapPoint(x = $.x, y = $.y))
    } else None
  override def fromRelativePosition(p: Position) =
    if (isRelativePositionInBounds(p))
      fromAbsolutePosition(p.withRelative(absolutePosition)).get
    else
      throw new IndexOutOfBoundsException(p.toString)

  override def isInBounds(mp: MapPoint) = {
    val offset = properties.getCurrentOffset
    properties.getCurrentMapSize.isInBounds(mp) &&
        mp.x >= offset.x && mp.y >= offset.y &&
        mp.x - offset.x < graphicsSize.width && mp.y - offset.y < graphicsSize.height
  }
  override def isAbsolutePositionInBounds(p: Position): Boolean =
    p.inSizedContainer(absolutePosition, graphicsSize)
  override def isRelativePositionInBounds(p: Position): Boolean =
    isAbsolutePositionInBounds(p.withRelative(absolutePosition))
  override def center = {
    val offset = properties.getCurrentOffset
    MapPoint(x = offset.x + graphicsSize.width / 2, y = offset.y + graphicsSize.height / 2)
  }
}

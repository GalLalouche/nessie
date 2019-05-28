package com.nessie.view.zirconview.map

import com.nessie.model.map.{Direction, MapPoint}
import common.rich.RichT._
import org.hexworks.zircon.api.data.{Position, Size}
import org.hexworks.zircon.api.Positions
import com.nessie.view.zirconview.ZirconUtils._

private[zirconview] case class MapGridPoint(
    private val mapGridPosition: Position,
    private val graphicsSize: Size,
    mapPoint: MapPoint,
) {
  private def inBounds(mapPoint: MapPoint): Boolean =
    mapPoint.x >= 0 && mapPoint.y >= 0 &&
        mapPoint.x < graphicsSize.getWidth && mapPoint.y < graphicsSize.getHeight
  def go(d: Direction): Option[MapGridPoint] =
    mapPoint.go(d).optFilter(inBounds).map(mp => copy(mapPoint = mp))
  val relativePosition: Position = Positions.create(mapPoint.x, mapPoint.y)
  val absolutePosition: Position = relativePosition.withRelative(mapGridPosition)
}
private[zirconview] object MapGridPoint {
  def withMapGridPosition(mapGridPosition: Position, graphicsSize: Size)(mp: MapPoint) =
    new MapGridPoint(mapGridPosition, graphicsSize, mp)
  def fromPosition(
      mapGridPosition: Position, graphicsSize: Size)(absolutePosition: Position): Option[MapGridPoint] =
    MapGridPoint(
      mapGridPosition,
      graphicsSize,
      absolutePosition.withInverseRelative(mapGridPosition).mapTo(p => MapPoint(x = p.getX, y = p.getY)),
    ).optFilter(p => p.inBounds(p.mapPoint))
}

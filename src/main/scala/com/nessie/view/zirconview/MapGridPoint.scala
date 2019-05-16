package com.nessie.view.zirconview

import com.nessie.model.map.{Direction, MapPoint}
import ZirconUtils._
import common.rich.RichT._
import org.hexworks.zircon.api.data.{Position, Size}
import org.hexworks.zircon.api.Positions

private case class MapGridPoint(
    private val mapGridPosition: Position,
    private val mapSize: Size,
    mapPoint: MapPoint,
) {
  private def inBounds(mapPoint: MapPoint): Boolean =
    mapPoint.x >= 0 && mapPoint.y >= 0 && mapPoint.x < mapSize.getWidth && mapPoint.y < mapSize.getHeight
  def go(d: Direction): Option[MapGridPoint] =
    mapPoint.go(d).opt.filter(inBounds).map(mp => copy(mapPoint = mp))
  val relativePosition: Position = Positions.create(mapPoint.x, mapPoint.y)
  val absolutePosition: Position = relativePosition.withRelative(mapGridPosition)
}
private object MapGridPoint {
  def withMapGridPosition(mapGridPosition: Position, mapSize: Size)(mp: MapPoint) =
    new MapGridPoint(mapGridPosition, mapSize, mp)
  def fromPosition(mapGridPosition: Position, mapSize: Size)(absolutePosition: Position): Option[MapGridPoint] =
    MapGridPoint(
      mapGridPosition,
      mapSize,
      absolutePosition.withInverseRelative(mapGridPosition).mapTo(p => MapPoint(x = p.getX, y = p.getY)),
    ).opt.filter(p => p.inBounds(p.mapPoint))
}

package com.nessie.view.zirconview.map

import com.nessie.model.map.{Direction, GridSize, MapPoint}
import com.nessie.view.zirconview.ZirconUtils._
import common.rich.RichT._
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.data.{Position, Size}

private class MapPointConverterImpl(
    getCurrentOffset: () => MapPoint,
    getCurrentMapSize: () => GridSize,
    absolutePosition: Position,
    graphicsSize: Size,
) extends MapPointConverter {

  import com.nessie.view.zirconview.map.MapPointConverterImpl.MapGridPoint

  private def toMapGridPoint: MapPoint => MapGridPoint =
    MapGridPoint.withMapGridPosition(absolutePosition, graphicsSize)
  override def toAbsolutePosition(mp: MapPoint) = synchronized {toMapGridPoint(mp).absolutePosition}
  override def toRelativePosition(mp: MapPoint) = toAbsolutePosition(mp).withInverseRelative(absolutePosition)
  override def fromAbsolutePosition(p: Position) = synchronized {
    p.optFilter(_.inSizedContainer(absolutePosition, graphicsSize))
        .map(_.withRelative(absolutePosition))
        .flatMap(MapGridPoint.fromPosition(absolutePosition, graphicsSize))
        .map(_.mapPoint)
  }
  override def isInBounds(mp: MapPoint) = toMapGridPoint(mp).inBounds(mp)
}

private object MapPointConverterImpl {
  private case class MapGridPoint(
      private val mapGridPosition: Position,
      private val graphicsSize: Size,
      mapPoint: MapPoint,
  ) {
    def inBounds(mapPoint: MapPoint): Boolean =
      mapPoint.x >= 0 && mapPoint.y >= 0 &&
          mapPoint.x < graphicsSize.getWidth && mapPoint.y < graphicsSize.getHeight
    def go(d: Direction): Option[MapGridPoint] =
      mapPoint.go(d).optFilter(inBounds).map(mp => copy(mapPoint = mp))
    val relativePosition: Position = Positions.create(mapPoint.x, mapPoint.y)
    val absolutePosition: Position = relativePosition.withRelative(mapGridPosition)
  }
  private object MapGridPoint {
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
}

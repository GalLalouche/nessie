package com.nessie.view.zirconview.map

import com.nessie.model.map.{Direction, MapPoint}
import com.nessie.model.map.fov.FogOfWar
import com.nessie.view.zirconview.ZirconUtils._
import common.rich.RichT._
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.graphics.{Layer, TileGraphics}

private class ZirconMapImpl(
    private var currentMap: FogOfWar,
    private val view: MapView,
    private var currentOffset: MapPoint,
) extends ZirconMap {
  override def getCurrentMap: FogOfWar = synchronized {currentMap}
  private val mapGridPosition: Position = view.position
  override val graphics: TileGraphics = view.graphics
  override val fogOfWarLayer: Layer = view.fogOfWarLayer

  private def internalUpdate(map: FogOfWar, offset: MapPoint = currentOffset): Unit = synchronized {
    currentMap = map
    currentOffset = offset
    view.updateTiles(map, offset)
  }
  def update(map: FogOfWar): Unit = synchronized {internalUpdate(map)}

  override def toAbsolutePosition(mp: MapPoint) = synchronized {toMapGridPoint(mp).absolutePosition}

  override def fromAbsolutePosition(p: Position) = synchronized {
    p.opt.filter(_.inSizedContainer(mapGridPosition, view.size))
        .map(_.withRelative(toPosition(currentOffset)))
        .flatMap(MapGridPoint.fromPosition(mapGridPosition, currentMap.size.toZirconSize))
        .map(_.mapPoint)
  }

  def scroll(n: Int, direction: Direction): Unit = synchronized {
    Scroller(n, direction, currentOffset, graphics.size, currentMap.size)
        .foreach(internalUpdate(currentMap, _))
  }
}




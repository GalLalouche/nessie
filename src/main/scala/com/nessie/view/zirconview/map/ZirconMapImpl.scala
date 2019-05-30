package com.nessie.view.zirconview.map

import com.nessie.model.map.fov.FogOfWar
import com.nessie.model.map.{Direction, MapPoint}
import com.nessie.view.zirconview.ZirconUtils._
import org.hexworks.zircon.api.color.ANSITileColor
import org.hexworks.zircon.api.graphics.{Layer, TileGraphics}

private class ZirconMapImpl(
    private var currentMap: FogOfWar,
    view: MapView,
    private var currentOffset: MapPoint,
) extends ZirconMap with ScrollableMapViewProperties {
  private var movables: Iterable[MapPoint] = Nil
  override def getCurrentMap: FogOfWar = synchronized(currentMap)
  override val graphics: TileGraphics = view.graphics
  override val fogOfWarLayer: Layer = view.fogOfWarLayer

  private def internalUpdate(map: FogOfWar, offset: MapPoint = currentOffset): Unit = synchronized {
    currentMap = map
    currentOffset = offset
    view.updateTiles(map, offset)
  }
  override def update(map: FogOfWar): Unit = synchronized(internalUpdate(map))

  override def getCurrentOffset = synchronized(currentOffset)
  override def getCurrentMapSize = synchronized(currentMap.size)
  override val graphicsSize = graphics.size
  override val mapPointConverter: MapPointConverter = new MapPointConverterImpl(this, view.position)

  private val scroller = new Scroller(this)
  override def scroll(n: Int, direction: Direction): Unit = synchronized {
    scroller(n, direction).foreach(internalUpdate(currentMap, _))
    highlightMovable(movables)
  }
  override def center(mp: MapPoint): Unit =
    synchronized(internalUpdate(currentMap, scroller.center(mp, mapPointConverter.center)))

  def highlightMovable(mps: Iterable[MapPoint]): Unit = synchronized {
    movables = mps
    mps.iterator.flatMap(mapPointConverter.toRelativePosition)
        .foreach(tileLens(_).modify(_.withBackgroundColor(ANSITileColor.GREEN))(graphics))
  }
}




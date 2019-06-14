package com.nessie.view.zirconview.map

import com.nessie.model.map.{BattleMap, Direction, MapPoint}
import com.nessie.model.map.fov.FogOfWar
import com.nessie.view.zirconview.ZirconUtils._
import com.nessie.view.MapAndPlayerFog
import org.hexworks.zircon.api.color.ANSITileColor
import org.hexworks.zircon.api.graphics.{Layer, TileGraphics}

private class ZirconMapImpl(
    private var currentMapAndPlayerFog: MapAndPlayerFog,
    view: MapView,
    private var currentOffset: MapPoint,
) extends ZirconMap with ScrollableMapViewProperties {
  private var movables: Iterable[MapPoint] = Nil
  override def getCurrentMap: BattleMap = synchronized(currentMapAndPlayerFog.map)
  override def getCurrentPlayerFogOfWar: FogOfWar = synchronized(currentMapAndPlayerFog.fogOfWar)
  override val graphics: TileGraphics = view.graphics
  override val fogOfWarLayer: Layer = view.fogOfWarLayer

  private def internalUpdate(mapAndPlayerFog: MapAndPlayerFog, offset: MapPoint = currentOffset): Unit =
    synchronized {
      currentMapAndPlayerFog = mapAndPlayerFog
      currentOffset = offset
      view.updateTiles(mapAndPlayerFog, offset)
    }
  override def update(mapf: MapAndPlayerFog): Unit = synchronized(internalUpdate(mapf))

  override def getCurrentOffset = synchronized(currentOffset)
  override def getCurrentMapSize = synchronized(getCurrentMap.size)
  override val graphicsSize = graphics.size
  override val mapPointConverter: MapPointConverter = new MapPointConverterImpl(this, view.position)

  private val scroller = new Scroller(this)
  override def scroll(n: Int, direction: Direction): Unit = synchronized {
    scroller(n, direction).foreach(internalUpdate(currentMapAndPlayerFog, _))
    highlightMovable(movables)
  }
  override def center(mp: MapPoint): Unit = synchronized(
    internalUpdate(currentMapAndPlayerFog, scroller.center(mp, mapPointConverter.center)))

  def highlightMovable(mps: Iterable[MapPoint]): Unit = synchronized {
    movables = mps
    mps.iterator.flatMap(mapPointConverter.toRelativePosition)
        .foreach(tileLens(_).modify(_.withBackgroundColor(ANSITileColor.GREEN))(graphics))
  }
}




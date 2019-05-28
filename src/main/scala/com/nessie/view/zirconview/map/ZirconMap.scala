package com.nessie.view.zirconview.map

import com.nessie.model.map.{Direction, MapPoint}
import com.nessie.model.map.fov.{FogOfWar, FovCalculator}
import com.nessie.model.map.fov.FogStatus.{Hidden, Visible}
import com.nessie.view.zirconview.{ZirconConstants, ZirconMapCustomizer}
import com.nessie.view.zirconview.ZirconUtils._
import common.rich.RichT._
import org.hexworks.zircon.api.{DrawSurfaces, Layers, Positions, Sizes}
import org.hexworks.zircon.api.color.ANSITileColor
import org.hexworks.zircon.api.data.{Position, Size}
import org.hexworks.zircon.api.graphics.{Layer, TileGraphics}
import org.hexworks.zircon.api.screen.Screen
import rx.lang.scala.Observable

// This map has to be mutable, since redrawing the same graphics causes nasty refresh bugs in Zircon :\
private[zirconview] trait ZirconMap extends MapPointConverter {
  protected final def toPosition(mp: MapPoint): Position = Positions.create(mp.x, mp.y)
  def getCurrentMap: FogOfWar
  def graphics: TileGraphics
  def fogOfWarLayer: Layer
  def buildLayer = fogOfWarLayer.clearCopy

  def updateViewAndFog(mp: Option[MapPoint]): Unit = synchronized {
    update(mp.fold(getCurrentMap)(mp =>
      getCurrentMap.updateVisible(FovCalculator(getCurrentMap.map).getVisiblePointsFrom(mp, 10).toSet)))
  }
  def hideAll(): Unit = synchronized {update(getCurrentMap.foldPoints(_.place(_, Hidden)))}
  def showAll(): Unit = synchronized {update(getCurrentMap.foldPoints(_.place(_, Visible)))}

  def toMapGridPoint: MapPoint => MapGridPoint =
    MapGridPoint.withMapGridPosition(fogOfWarLayer.getPosition, graphics.getSize)
  def highlightMovable(mps: Iterable[MapPoint]): Unit = mps.iterator.map(toPosition)
      .foreach(tileLens(_).modify(_.withBackgroundColor(ANSITileColor.GREEN))(graphics))

  def mouseEvents(screen: Screen): Observable[Option[MapPoint]] =
    screen.mouseActions().map(_.getPosition |> fromAbsolutePosition)
  def update(map: FogOfWar): Unit
  def scroll(n: Int, direction: Direction): Unit
}

private[zirconview] object ZirconMap {
  def create(map: FogOfWar, c: ZirconMapCustomizer, mapGridPosition: Position, maxSize: Size): ZirconMap = {
    val graphics: TileGraphics = DrawSurfaces.tileGraphicsBuilder
        .withSize(Sizes.create(math.min(maxSize.width, map.width), math.min(map.height, maxSize.height)))
        .build
    val fogOfWarLayer = Layers.newBuilder
        .withOffset(mapGridPosition)
        .withSize(graphics.getSize)
        .build
    val view = new MapView(graphics, fogOfWarLayer, c, ZirconConstants.Theme)
    val initialOffset = MapPoint(0, 0)
    new ZirconMapImpl(map, view <| (_.updateTiles(map, initialOffset)), currentOffset = initialOffset)
  }
}


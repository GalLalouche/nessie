package com.nessie.view.zirconview.map

import com.nessie.model.map.{BattleMap, Direction, MapPoint, VectorGrid}
import com.nessie.model.map.fov.{FogOfWar, FovCalculator}
import com.nessie.model.map.fov.FogStatus.{Hidden, Visible}
import com.nessie.view.zirconview.{ZirconConstants, ZirconMapCustomizer}
import com.nessie.view.zirconview.ZirconUtils._
import com.nessie.view.MapAndPlayerFog
import common.rich.RichT._
import org.hexworks.zircon.api.{DrawSurfaces, Layers}
import org.hexworks.zircon.api.data.{Position, Size}
import org.hexworks.zircon.api.graphics.{Layer, TileGraphics}
import org.hexworks.zircon.api.screen.Screen
import rx.lang.scala.Observable

// This map has to be mutable, since redrawing the same graphics causes nasty refresh bugs in Zircon :\
private[zirconview] trait ZirconMap {
  def getCurrentMap: BattleMap
  def getCurrentPlayerFogOfWar: FogOfWar
  def graphics: TileGraphics
  def fogOfWarLayer: Layer
  def buildLayer = fogOfWarLayer.clearCopy

  def updateViewAndFog(mp: Option[MapPoint]): Unit = synchronized {
    update(MapAndPlayerFog(
      map = getCurrentMap,
      fogOfWar = mp.fold(getCurrentPlayerFogOfWar)(mp => getCurrentPlayerFogOfWar
          .updateVisible(FovCalculator(getCurrentMap).getVisiblePointsFrom(mp, 10).toSet))
    )
    )
  }
  def hideAll(): Unit = synchronized {
    update(MapAndPlayerFog(getCurrentMap, getCurrentPlayerFogOfWar.fill(Hidden)))
  }
  def showAll(): Unit = synchronized {
    update(MapAndPlayerFog(getCurrentMap, getCurrentPlayerFogOfWar.fill(Visible)))
  }

  def mapPointConverter: MapPointConverter
  def highlightMovable(mps: Iterable[MapPoint]): Unit

  def mouseEvents(screen: Screen): Observable[Option[MapPoint]] =
    screen.mouseActions().map(_.getPosition |> mapPointConverter.fromAbsolutePosition)
  def update(mapf: MapAndPlayerFog): Unit
  def scroll(n: Int, direction: Direction): Unit
  def center(mp: MapPoint): Unit
}

private[zirconview] object ZirconMap {
  def create(c: ZirconMapCustomizer, mapGridPosition: Position, graphicsSize: Size): ZirconMap = {
    val graphics: TileGraphics = DrawSurfaces.tileGraphicsBuilder
        .withSize(graphicsSize)
        .build
    val fogOfWarLayer = Layers.newBuilder
        .withOffset(mapGridPosition)
        .withSize(graphics.getSize)
        .build
    val view = new MapView(graphics, fogOfWarLayer, c, ZirconConstants.Theme)
    val emptyMap = BattleMap.create(VectorGrid, 10, 10)
    new ZirconMapImpl(
      MapAndPlayerFog(emptyMap, FogOfWar.allVisible(emptyMap)), view, currentOffset = MapPoint(0, 0))
  }
}


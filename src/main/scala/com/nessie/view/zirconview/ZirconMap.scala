package com.nessie.view.zirconview

import com.nessie.model.map.{CombatUnitObject, Direction, EmptyMapObject, FullWall, MapPoint}
import com.nessie.model.map.fov.{FogOfWar, FogStatus, FovCalculator}
import com.nessie.model.map.fov.FogStatus.{Hidden, Visible}
import com.nessie.view.zirconview.ZirconMap._
import com.nessie.view.zirconview.ZirconUtils._
import common.rich.collections.RichIterator._
import common.rich.RichT._
import org.hexworks.zircon.api.{DrawSurfaces, Layers, Positions, Sizes, Tiles}
import org.hexworks.zircon.api.color.ANSITileColor
import org.hexworks.zircon.api.data.{Position, Size, Tile}
import org.hexworks.zircon.api.graphics.{Layer, Symbols, TileGraphics}
import org.hexworks.zircon.api.screen.Screen
import rx.lang.scala.Observable

// TODO split this class, it's getting too big
// This map has to be mutable, since Redrawing the same graphics causes nasty refresh bugs in Zircon :\
private class ZirconMap(
    private var currentMap: FogOfWar,
    c: ZirconMapCustomizer,
    val graphics: TileGraphics,
    val fogOfWarLayer: Layer,
    mapGridPosition: Position,
) extends MapPointConverter {
  private var currentOffset = MapPoint(0, 0)
  def offset = synchronized {currentOffset}
  private def toPosition(mp: MapPoint): Position = Positions.create(mp.x, mp.y)
  val graphicsSize: Size = graphics.getSize
  def mapSize: Size = synchronized {Sizes.create(currentMap.width, currentMap.height)}
  private val width = graphicsSize.width
  private val height = graphicsSize.height
  private def updateTiles(): Unit = synchronized {
    for (x <- currentOffset.x until currentOffset.x + width; y <- currentOffset.y until currentOffset.y + height) {
      val mp = MapPoint(x = x, y = y)
      val obj = currentMap.map(mp)
      val pos = toPosition(mp).withInverseRelative(toPosition(currentOffset))
      val tile =
        c.getTile.lift(obj).getOrElse((obj match {
          case EmptyMapObject => Tiles.newBuilder.withCharacter(Symbols.INTERPUNCT)
          case FullWall => Tiles.newBuilder.withCharacter('#')
          case CombatUnitObject(u) => Tiles.newBuilder.withCharacter(u.metadata.name.head)
        })
            .withBackgroundColor(theme.getSecondaryBackgroundColor)
            .withForegroundColor(theme.getSecondaryForegroundColor)
        ).build
      graphics.setTileAt(pos, tile)
      fogOfWarLayer.setAbsoluteTileAt(pos.withRelative(mapGridPosition),
        Tiles.newBuilder()
            .withBackgroundColor(ANSITileColor.BLACK.toData.multiplyAlphaBy(currentMap(mp) match {
              case FogStatus.Visible => 0.0
              case FogStatus.Hidden => 1.0
              case FogStatus.Fogged => 0.5
            }))
            .build)
    }
  }

  def update(map: => FogOfWar): Unit = {
    currentMap = map
    updateTiles()
  }
  def updateViewAndFog(mp: Option[MapPoint]): Unit = synchronized {
    update(mp.fold(currentMap)(mp =>
      currentMap.updateVisible(FovCalculator(currentMap.map).getVisiblePointsFrom(mp, 10).toSet)))
  }
  def hideAll(): Unit = update(currentMap.foldPoints(_.place(_, Hidden)))
  def showAll(): Unit = update(currentMap.foldPoints(_.place(_, Visible)))

  def getCurrentBattleMap: FogOfWar = synchronized {currentMap}

  def mouseEvents(screen: Screen): Observable[Option[MapPoint]] =
    screen.mouseActions().map(_.getPosition |> fromAbsolutePosition)

  def highlightMovable(mps: Iterable[MapPoint]): Unit = mps.iterator.map(toPosition)
      .foreach(tileLens(_).modify(_.withBackgroundColor(ANSITileColor.GREEN))(graphics))
  def toMapGridPoint: MapPoint => MapGridPoint =
    MapGridPoint.withMapGridPosition(mapGridPosition, graphicsSize)
  def buildLayer: Layer = ZirconMap.buildLayer(mapGridPosition, graphics)
  def tileAt(mapPoint: MapPoint): Tile = graphics.getTileAt(toPosition(mapPoint)).get.asInstanceOf[Tile]
  override def toAbsolutePosition(mp: MapPoint) = toMapGridPoint(mp).absolutePosition

  override def fromAbsolutePosition(p: Position) =
    p.opt.filter(_.inSizedContainer(mapGridPosition, graphicsSize))
        .map(_.withRelative(toPosition(offset)))
        .flatMap(MapGridPoint.fromPosition(mapGridPosition, mapSize))
        .map(_.mapPoint)

  private def offsetInBounds(mp: MapPoint): Boolean = synchronized {
    mp.x >= 0 && mp.y >= 0 && mp.x + width <= currentMap.width && mp.y + height <= currentMap.height
  }
  def scroll(n: Int, direction: Direction): Unit = synchronized {
    // TODO RichIterator.apply
    Iterator.iterate(currentOffset)(_ go direction).takeWhile(offsetInBounds).take(n + 1).lastOption
        .filter(offsetInBounds)
        .foreach {newOffset =>
          currentOffset = newOffset
          updateTiles()
        }
  }
}

private object ZirconMap {
  private val theme = ZirconConstants.Theme
  private def buildLayer(position: Position, graphics: TileGraphics) = Layers.newBuilder
      .withOffset(position)
      .withSize(graphics.getSize)
      .build
  def create(map: FogOfWar, c: ZirconMapCustomizer, mapGridPosition: Position, maxSize: Size): ZirconMap = {
    val graphics: TileGraphics = DrawSurfaces.tileGraphicsBuilder
        .withSize(Sizes.create(math.min(maxSize.width, map.width), math.min(map.height, maxSize.height)))
        .build
    new ZirconMap(map, c, graphics, buildLayer(mapGridPosition, graphics), mapGridPosition).<|(_.updateTiles())
  }
}

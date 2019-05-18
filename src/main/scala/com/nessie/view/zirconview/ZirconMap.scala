package com.nessie.view.zirconview

import com.nessie.model.map.{BattleMap, CombatUnitObject, EmptyMapObject, FullWall, MapPoint}
import com.nessie.model.map.fov.FovCalculator
import com.nessie.view.zirconview.ZirconMap._
import com.nessie.view.zirconview.ZirconUtils._
import common.rich.RichT._
import org.hexworks.zircon.api.{DrawSurfaces, Layers, Positions, Sizes, Tiles}
import org.hexworks.zircon.api.color.ANSITileColor
import org.hexworks.zircon.api.data.{Position, Size, Tile}
import org.hexworks.zircon.api.graphics.{Layer, Symbols, TileGraphics}
import org.hexworks.zircon.api.screen.Screen
import rx.lang.scala.Observable

// This map has to be mutable, since Redrawing the same graphics causes nasty refresh bugs in Zircon :\
private class ZirconMap(
    private var currentMap: BattleMap,
    c: ZirconMapCustomizer,
    val graphics: TileGraphics,
    mapGridPosition: Position,
) extends MapPointConverter {
  private def toPosition(mp: MapPoint): Position = Positions.create(mp.x, mp.y)
  def size: Size = graphics.getSize
  private def updateTiles(): Unit = synchronized {updateTiles(true.const)}
  private def updateTiles(isVisible: MapPoint => Boolean): Unit = synchronized {
    currentMap.objects.map {
      case (mp, obj) =>
        toPosition(mp) -> (
            if (isVisible(mp))
              c.getTile.lift(obj).getOrElse(obj match {
                case EmptyMapObject => Tiles.newBuilder.withCharacter(Symbols.INTERPUNCT)
                case FullWall => Tiles.newBuilder.withCharacter('#')
                case CombatUnitObject(u) => Tiles.newBuilder.withCharacter(u.metadata.name.head)
              })
                  .withBackgroundColor(theme.getSecondaryBackgroundColor)
                  .withForegroundColor(theme.getSecondaryForegroundColor)
                  .build
            else
              Unrevealed
            )
    }.foreach((graphics.setTileAt _).tupled)
  }

  def drawFov(mp: Option[MapPoint]): Unit = synchronized {
    mp.fold(updateTiles())(FovCalculator(currentMap).getVisiblePointsFrom(_, 10).toSet |> updateTiles)
  }
  def update(map: BattleMap): Unit = synchronized {
    currentMap = map
    updateTiles()
  }

  def getCurrentBattleMap: BattleMap = synchronized {currentMap}

  def mouseEvents(screen: Screen): Observable[Option[MapPoint]] =
    screen.mouseActions().map(_.getPosition |> fromAbsolutePosition)

  def highlightMovable(mps: Iterable[MapPoint]): Unit = mps.iterator.map(toPosition)
      .foreach(tileLens(_).modify(_.withBackgroundColor(ANSITileColor.GREEN))(graphics))
  def toMapGridPoint: MapPoint => MapGridPoint = MapGridPoint.withMapGridPosition(mapGridPosition, size)
  def buildLayer: Layer = Layers.newBuilder
      .withOffset(mapGridPosition)
      .withSize(graphics.getSize)
      .build
  def tileAt(mapPoint: MapPoint): Tile = graphics.getTileAt(toPosition(mapPoint)).get.asInstanceOf[Tile]
  override def toAbsolutePosition(mp: MapPoint) = toMapGridPoint(mp).absolutePosition
  override def fromAbsolutePosition(p: Position) =
    MapGridPoint.fromPosition(mapGridPosition, size)(p).map(_.mapPoint)
}

private object ZirconMap {
  private val theme = ZirconConstants.Theme
  private val Unrevealed = Tiles.newBuilder
      .withCharacter(' ')
      .withBackgroundColor(ANSITileColor.BLACK)
      .buildCharacterTile
  def create(map: BattleMap, c: ZirconMapCustomizer, mapGridPosition: Position) = {
    val graphics: TileGraphics = DrawSurfaces.tileGraphicsBuilder
        .withSize(Sizes.create(map.width, map.height))
        .build
    new ZirconMap(map, c, graphics, mapGridPosition).<|(_.updateTiles())
  }
}

package com.nessie.view.zirconview

import com.nessie.model.map.{BattleMap, CombatUnitObject, EmptyMapObject, FullWall, MapPoint}
import com.nessie.model.map.fov.FovCalculator
import com.nessie.view.zirconview.ZirconMap._
import com.nessie.view.zirconview.ZirconUtils._
import common.rich.RichT._
import org.hexworks.zircon.api.{DrawSurfaces, Sizes, Tiles}
import org.hexworks.zircon.api.color.ANSITileColor
import org.hexworks.zircon.api.graphics.{Symbols, TileGraphics}

// This map has to be mutable, since Redrawing the same graphics causes nasty refresh bugs in Zircon :\
private class ZirconMap(
    private var currentMap: BattleMap,
    c: ZirconMapCustomizer,
    val graphics: TileGraphics,
) {
  private def updateTiles(): Unit = updateTiles(true.const)
  private def updateTiles(isVisible: MapPoint => Boolean): Unit = synchronized {
    currentMap.objects.map {
      case (mp, obj) =>
        mp.toPosition -> (
            if (isVisible(mp))
              c.getTile.lift(obj).getOrElse(obj match {
                case EmptyMapObject => Tiles.newBuilder().withCharacter(Symbols.INTERPUNCT)
                case FullWall => Tiles.newBuilder().withCharacter('#')
                case CombatUnitObject(u) => Tiles.newBuilder().withCharacter(u.metadata.name.head)
              })
                  .withBackgroundColor(theme.getSecondaryBackgroundColor)
                  .withForegroundColor(theme.getSecondaryForegroundColor)
                  .build()
            else
              Unrevealed
            )
    }
        .foreach((graphics.setTileAt _).tupled)
  }

  def drawFov(mp: Option[MapPoint]): Unit =
    mp.fold(updateTiles())(FovCalculator(currentMap).getVisiblePointsFrom(_, 10).toSet |> updateTiles)
  def update(map: BattleMap): Unit = synchronized {
    currentMap = map
    updateTiles()
  }

  def map: BattleMap = synchronized {currentMap}
}

private object ZirconMap {
  private val theme = ZirconConstants.Theme
  private val Unrevealed = Tiles.newBuilder()
      .withCharacter(' ')
      .withBackgroundColor(ANSITileColor.BLACK)
      .buildCharacterTile()
  def create(map: BattleMap, c: ZirconMapCustomizer) = {
    val graphics: TileGraphics = DrawSurfaces.tileGraphicsBuilder()
        .withSize(Sizes.create(map.width, map.height))
        .build()
    new ZirconMap(map, c, graphics).<|(_.updateTiles())
  }
}

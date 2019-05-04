package com.nessie.view.zirconview

import com.nessie.model.map.{BattleMap, CombatUnitObject, EmptyMapObject, FullWall}
import common.rich.RichTuple._
import org.hexworks.zircon.api.{DrawSurfaces, Positions, Sizes, Tiles}
import org.hexworks.zircon.api.graphics.{Symbols, TileGraphics}

private object ZirconMap {
  private val theme = ZirconConstants.Theme
  def createGraphics(map: BattleMap, c: ZirconMapCustomizer): TileGraphics = {
    val $ = DrawSurfaces.tileGraphicsBuilder()
        .withSize(Sizes.create(map.width, map.height))
        .build()
    map.objects.map(_.map2(
      mp => Positions.create(mp.x, mp.y),
      obj => c.getTile.lift(obj).getOrElse(obj match {
        case EmptyMapObject => Tiles.newBuilder().withCharacter(Symbols.INTERPUNCT)
        case FullWall => Tiles.newBuilder().withCharacter('#')
        case CombatUnitObject(u) => Tiles.newBuilder().withCharacter(u.metadata.name.head)
      })
          .withBackgroundColor(theme.getSecondaryBackgroundColor)
          .withForegroundColor(theme.getSecondaryForegroundColor)
          .build()
    ))
        .foreach(($.setTileAt _).tupled)

    $
  }
}

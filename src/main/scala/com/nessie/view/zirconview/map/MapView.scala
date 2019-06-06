package com.nessie.view.zirconview.map

import com.nessie.model.map.{CombatUnitObject, EmptyMapObject, FullWall, MapPoint}
import com.nessie.model.map.fov.{FogOfWar, FogStatus}
import com.nessie.view.zirconview.ZirconMapCustomizer
import com.nessie.view.zirconview.ZirconUtils._
import org.hexworks.zircon.api.{Positions, Tiles}
import org.hexworks.zircon.api.color.ANSITileColor
import org.hexworks.zircon.api.component.ColorTheme
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.graphics.{Layer, Symbols, TileGraphics}

private class MapView(
    val graphics: TileGraphics,
    val fogOfWarLayer: Layer,
    c: ZirconMapCustomizer,
    theme: ColorTheme,
) {
  private def toPosition(mp: MapPoint): Position = Positions.create(mp.x, mp.y)
  val size = graphics.getSize
  val position = fogOfWarLayer.getPosition
  def buildLayer: Layer = fogOfWarLayer.clearCopy
  private val width = graphics.width
  private val height = graphics.height
  def updateTiles(fog: FogOfWar, viewOffset: MapPoint): Unit = synchronized {
    val map = fog.map
    for (
      x <- viewOffset.x until viewOffset.x + width;
      y <- viewOffset.y until viewOffset.y + height;
      mp = MapPoint(x = x, y = y)
      if map.isInBounds(mp)
    ) {
      val obj = map(mp)
      val pos = toPosition(mp).withInverseRelative(toPosition(viewOffset))
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
      fogOfWarLayer.setAbsoluteTileAt(pos.withRelative(fogOfWarLayer.getPosition),
        Tiles.newBuilder()
            .withBackgroundColor(ANSITileColor.BLACK.toData.multiplyAlphaBy(fog(mp) match {
              case FogStatus.Visible => 0.0
              case FogStatus.Hidden => 1.0
              case FogStatus.Fogged => 0.5
            }))
            .build)
    }
  }
}

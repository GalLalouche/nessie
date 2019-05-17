package com.nessie.view.zirconview

import org.hexworks.zircon.api.color.TileColor
import org.hexworks.zircon.api.component.ColorTheme

/** For easy partial modification of ColorTheme. */
private case class ColorThemeData(
    override val getAccentColor: TileColor,
    override val getPrimaryForegroundColor: TileColor,
    override val getSecondaryForegroundColor: TileColor,
    override val getPrimaryBackgroundColor: TileColor,
    override val getSecondaryBackgroundColor: TileColor,
) extends ColorTheme

private object ColorThemeData {
  def from(colorTheme: ColorTheme): ColorThemeData = ColorThemeData(
    getAccentColor = colorTheme.getAccentColor,
    getPrimaryForegroundColor = colorTheme.getPrimaryForegroundColor,
    getSecondaryForegroundColor = colorTheme.getSecondaryForegroundColor,
    getPrimaryBackgroundColor = colorTheme.getPrimaryBackgroundColor,
    getSecondaryBackgroundColor = colorTheme.getSecondaryBackgroundColor,
  )
}

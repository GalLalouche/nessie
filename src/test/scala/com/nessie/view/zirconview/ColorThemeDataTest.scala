package com.nessie.view.zirconview

import common.AuxSpecs
import org.hexworks.zircon.api.ColorThemes
import org.hexworks.zircon.api.component.ColorTheme
import org.scalatest.FreeSpec

class ColorThemeDataTest extends FreeSpec with AuxSpecs {
  private def compareColorThemes(ct1: ColorTheme, ct2: ColorTheme): Unit = {
    ct1.getAccentColor shouldReturn ct2.getAccentColor
    ct1.getPrimaryForegroundColor shouldReturn ct2.getPrimaryForegroundColor
    ct1.getSecondaryForegroundColor shouldReturn ct2.getSecondaryForegroundColor
    ct1.getPrimaryBackgroundColor shouldReturn ct2.getPrimaryBackgroundColor
    ct1.getSecondaryBackgroundColor shouldReturn ct2.getSecondaryBackgroundColor
  }
  private val ct = ColorThemes.afterTheHeist
  "from" in {
    compareColorThemes(ct, ColorThemeData.from(ct))
  }
}

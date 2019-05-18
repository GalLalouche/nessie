package com.nessie.view.zirconview

import common.AuxSpecs
import org.hexworks.zircon.api.color.{ANSITileColor, TileColor}
import org.hexworks.zircon.api.TileColors
import org.scalatest.FreeSpec

class TileColorDataTest extends FreeSpec with AuxSpecs {
  private def compareTileColors(tc1: TileColor, tc2: TileColor): Unit = {
    tc1.getAlpha shouldReturn tc2.getAlpha
    tc1.getRed shouldReturn tc2.getRed
    tc1.getGreen shouldReturn tc2.getGreen
    tc1.getBlue shouldReturn tc2.getBlue
  }
  private val tc = ANSITileColor.BRIGHT_CYAN
  "from" in {
    compareTileColors(tc, TileColorData.from(tc))
  }
  "methods" in {
    val $ = TileColorData.from(tc)
    def apply(f: TileColor => TileColor): Unit = compareTileColors(f($), f(tc))
    apply(_.invert())
    apply(_.darkenByPercent(0.25))
    apply(_.lightenByPercent(0.25))
    apply(_.shade(0.25))
    apply(_.tint(0.25))
    $.isOpaque shouldReturn tc.isOpaque
    $.generateCacheKey() shouldReturn tc.generateCacheKey()
  }
  "multiplyAlphaBy" in {
    val $ = TileColorData.from(tc).copy(getAlpha = 100)
    compareTileColors($.multiplyAlphaBy(0.5), TileColors.create(tc.getRed, tc.getGreen, tc.getBlue, 50))
  }
}


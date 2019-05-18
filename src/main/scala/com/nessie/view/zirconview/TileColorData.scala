package com.nessie.view.zirconview

import org.hexworks.zircon.api.color.TileColor
import org.hexworks.zircon.api.TileColors

private case class TileColorData(
    getRed: Int,
    getGreen: Int,
    getBlue: Int,
    getAlpha: Int,
) extends TileColor {
  def multiplyAlphaBy(d: Double): TileColorData = copy(getAlpha = getAlpha.*(d).toInt)

  private val toTileColor: TileColor = TileColors.create(getRed, getGreen, getBlue, getAlpha)
  private def fromBase(f: TileColor => TileColor): TileColorData = TileColorData.from(f(toTileColor))
  override def darkenByPercent(v: Double): TileColorData = fromBase(_.darkenByPercent(v))
  override def invert(): TileColorData = fromBase(_.invert)
  override def lightenByPercent(v: Double): TileColorData = fromBase(_.lightenByPercent(v))
  override def shade(): TileColorData = fromBase(_.shade)
  override def shade(v: Double): TileColorData = fromBase(_.shade(v))
  override def tint(): TileColorData = fromBase(_.tint)
  override def tint(v: Double): TileColorData = fromBase(_.tint(v))
  override def generateCacheKey() = toTileColor.generateCacheKey()
  override def isOpaque = toTileColor.isOpaque
}

private object TileColorData {
  def from(tc: TileColor): TileColorData = TileColorData(
    getRed = tc.getRed,
    getGreen = tc.getGreen,
    getBlue = tc.getBlue,
    getAlpha = tc.getAlpha,
  )
}

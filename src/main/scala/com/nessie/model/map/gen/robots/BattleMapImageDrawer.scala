package com.nessie.model.map.gen.robots

import java.awt.image.BufferedImage
import java.awt.Color

import com.nessie.model.map.{BattleMap, MapPoint}
import com.nessie.model.map.gen.DemoImageViewer._

private object BattleMapImageDrawer {
  def apply(map: BattleMap): BufferedImage = {
    val gs = map.grid.size
    val $ = new BufferedImage(
      gs.width * ImageScale + 1, gs.height * ImageScale + 1, BufferedImage.TYPE_INT_ARGB
    )

    def applyColor(xRange: Iterable[Int], yRange: Iterable[Int], color: Color): Unit = for {
      x <- xRange.iterator
      y <- yRange.iterator
    } $.setRGB(x, y, color.getRGB)
    def colorOutline(): Unit = {
      val mp = MapPoint(0, 0)
      val tgs = translate(gs)
      applyColor(mp.x to mp.x + tgs.width, Vector(mp.y, mp.y + tgs.height), GridColor)
      applyColor(Vector(mp.x, mp.x + tgs.width), mp.y.to(mp.y + tgs.height), GridColor)
    }
    colorOutline()
    map.objects.foreach {
      case (mp, obj) => (obj match {
        case Tunnel(_) => Some(Color.DARK_GRAY)
        case Junction(_) => Some(Color.GRAY)
        case Room(_) => Some(Color.BLUE)
        case Door(_) => Some(Color.RED)
        case _ => None
      }).foreach {color =>
        val p = translate(mp)
        applyColor(p.x until p.x + ImageScale, p.y until p.y + ImageScale, color)
      }
    }
    $
  }
}

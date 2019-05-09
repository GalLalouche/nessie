package com.nessie.view.zirconview

import com.nessie.model.map.{BattleMap, MapPoint}
import common.rich.RichT._
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.Positions

private object ZirconUtils {
  implicit class RichPosition(private val $: Position) extends AnyVal {
    def withInverseRelative(other: Position): Position =
      $.withRelativeX(-other.getX).withRelativeY(-other.getY)
    def toMapPoint(map: BattleMap): Option[MapPoint] =
      MapPoint(x = $.getX, y = $.getY).opt.filter(map.isInBounds)
  }
  implicit class RichMapPoint(private val $: MapPoint) extends AnyVal {
    def toPosition: Position = Positions.create($.x, $.y)
  }
}
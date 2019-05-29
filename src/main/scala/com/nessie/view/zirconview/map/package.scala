package com.nessie.view.zirconview

import com.nessie.model.map.MapPoint
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.data.Position

package object map {
  private[map] implicit class RichMapPoint(private val $: MapPoint) extends AnyVal {
    def toPosition: Position = Positions.create($.x, $.y)
  }
}

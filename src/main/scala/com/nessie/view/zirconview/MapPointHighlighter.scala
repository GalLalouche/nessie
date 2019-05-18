package com.nessie.view.zirconview

import com.nessie.gm.GameState
import com.nessie.model.map.MapPoint

private trait MapPointHighlighter {
  def apply(gs: GameState, mp: MapPoint): Unit
  def clear(): Unit
}

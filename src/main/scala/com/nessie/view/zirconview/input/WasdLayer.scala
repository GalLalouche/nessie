package com.nessie.view.zirconview.input

import com.nessie.model.map.{Direction, MapPoint}
import com.nessie.view.zirconview.{MapGridPoint, SimpleKeyboardEvents, ZirconMap}
import common.rich.func.{MoreObservableInstances, ToMoreMonadPlusOps}
import org.hexworks.zircon.api.color.ANSITileColor
import org.hexworks.zircon.api.graphics.Layer

private object WasdLayer extends ToMoreMonadPlusOps with MoreObservableInstances {
  def create(
      keyboardEvents: SimpleKeyboardEvents,
      mapGrid: ZirconMap,
      observer: MapPoint => Any,
      initialLocation: MapGridPoint,
  ): Layer = {
    var currentLocation = initialLocation
    val movingUnitTile = mapGrid.tileAt(currentLocation.mapPoint).createCopy
        .withBackgroundColor(ANSITileColor.BRIGHT_CYAN)
        .withForegroundColor(ANSITileColor.BRIGHT_MAGENTA)
    val $ = mapGrid.buildLayer
    $.setTileAt(currentLocation.relativePosition, movingUnitTile)
    keyboardEvents
        .filter(MovementKeys.contains(_))
        .map {
          case 'W' | 'K' => Direction.Up
          case 'A' | 'H' => Direction.Left
          case 'S' | 'J' => Direction.Down
          case 'D' | 'L' => Direction.Right
        }
        // (_) is necessary in order to get the current variable value.
        .oMap(currentLocation.go(_))
        .foreach {newLocation =>
          currentLocation = newLocation
          $.clear()
          $.setTileAt(currentLocation.relativePosition, movingUnitTile)
        }
    keyboardEvents.filter(_ == 'I').foreach(_ => {observer(currentLocation.mapPoint)})
    $
  }

  private val MovementKeys = "WASDHJKL"
}

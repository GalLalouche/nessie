package com.nessie.view.zirconview.input

import com.nessie.model.map.{Direction, MapPoint}
import com.nessie.view.zirconview.{MapGridPoint, SimpleKeyboardEvents, ZirconMap}
import common.rich.func.{MoreObservableInstances, ToMoreMonadPlusOps}
import org.hexworks.zircon.api.color.ANSITileColor
import org.hexworks.zircon.api.graphics.Layer
import rx.lang.scala.Subscription

private class WasdLayer(val layer: Layer, subscriptions: Subscription*) {
  def clear(): Unit = subscriptions.foreach(_.unsubscribe())
}

private object WasdLayer
    extends ToMoreMonadPlusOps with MoreObservableInstances {
  def create(
      keyboardEvents: SimpleKeyboardEvents,
      mapGrid: ZirconMap,
      observer: MapPoint => Any,
      initialLocation: MapGridPoint,
  ): WasdLayer = {
    var currentLocation = initialLocation
    val movingUnitTile = mapGrid.tileAt(currentLocation.mapPoint).createCopy
        .withBackgroundColor(ANSITileColor.BRIGHT_CYAN)
        .withForegroundColor(ANSITileColor.BRIGHT_MAGENTA)
    val $ = mapGrid.buildLayer
    $.setTileAt(currentLocation.relativePosition, movingUnitTile)
    val movementSubscriptions = keyboardEvents
        .filter(MovementKeys.contains(_))
        .map {
          case 'W' | 'K' => Direction.Up
          case 'A' | 'H' => Direction.Left
          case 'S' | 'J' => Direction.Down
          case 'D' | 'L' => Direction.Right
        }
        // (_) is necessary in order to get the current variable value.
        .oMap(currentLocation.go(_))
        .subscribe {newLocation =>
          currentLocation = newLocation
          $.clear()
          $.setTileAt(currentLocation.relativePosition, movingUnitTile)
        }
    val openMenuSubscription =
      keyboardEvents.filter(_ == ' ').subscribe(_ => {observer(currentLocation.mapPoint)})
    new WasdLayer($, movementSubscriptions, openMenuSubscription)
  }

  private val MovementKeys = "WASDHJKL"
}

package com.nessie.view.zirconview.input

import com.nessie.model.map.{Direction, MapPoint}
import com.nessie.view.zirconview.SimpleKeyboardEvents
import com.nessie.view.zirconview.ZirconUtils._
import com.nessie.view.zirconview.map.MapGridPoint
import common.rich.func.{MoreObservableInstances, ToMoreMonadPlusOps}
import org.hexworks.zircon.api.color.ANSITileColor
import org.hexworks.zircon.api.graphics.Layer
import org.hexworks.zircon.api.Tiles
import rx.lang.scala.Subscription

private class MovementLayer(val layer: Layer, subscriptions: Subscription*) {
  def clear(): Unit = subscriptions.foreach(_.unsubscribe())
}

private object MovementLayer
    extends ToMoreMonadPlusOps with MoreObservableInstances {
  sealed trait MovementLayerAction
  case object OpenActionMenu extends MovementLayerAction
  case object Move extends MovementLayerAction
  type MovementLayerListener = MapPoint => MovementLayerAction => Unit
  def create(
      keyboardEvents: SimpleKeyboardEvents,
      layer: Layer,
      listener: MovementLayerListener,
      initialLocation: MapGridPoint,
      highlighter: MapPoint => Any,
  ): MovementLayer = {
    var currentLocation = initialLocation
    val movingUnitTile = Tiles.newBuilder
        .withBackgroundColor(ANSITileColor.BRIGHT_CYAN.toData.multiplyAlphaBy(0.5))
        .build
    layer.setTileAt(currentLocation.relativePosition, movingUnitTile)
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
          highlighter(currentLocation.mapPoint)
          layer.clear()
          layer.setTileAt(currentLocation.relativePosition, movingUnitTile)
        }
    val openMenuSubscription =
      keyboardEvents.collect({
        case ' ' => OpenActionMenu
        case 'M' => Move
        // (_) is necessary in order to get the current variable value.
      }).subscribe(listener(currentLocation.mapPoint)(_))
    new MovementLayer(layer, movementSubscriptions, openMenuSubscription)
  }

  private val MovementKeys = "WASDHJKL"
}

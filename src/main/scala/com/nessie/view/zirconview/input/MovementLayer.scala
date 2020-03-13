package com.nessie.view.zirconview.input

import com.nessie.model.map.{Direction, MapPoint}
import com.nessie.view.zirconview.SimpleKeyboardEvents
import com.nessie.view.zirconview.ZirconUtils._
import com.nessie.view.zirconview.map.MapPointConverter
import org.hexworks.zircon.api.Tiles
import org.hexworks.zircon.api.color.ANSITileColor
import org.hexworks.zircon.api.graphics.Layer
import rx.lang.scala.Subscription

import common.rich.func.MoreObservableInstances._
import common.rich.func.ToMoreMonadPlusOps._

import common.rich.RichT._

private class MovementLayer(val layer: Layer, subscriptions: Subscription*) {
  def clear(): Unit = subscriptions.foreach(_.unsubscribe())
}

private object MovementLayer {
  sealed trait MovementLayerAction
  case object OpenActionMenu extends MovementLayerAction
  case object Move extends MovementLayerAction
  type MovementLayerListener = MapPoint => MovementLayerAction => Unit
  def create(
      keyboardEvents: SimpleKeyboardEvents,
      layer: Layer,
      listener: MovementLayerListener,
      initialLocation: MapPoint,
      converter: MapPointConverter,
      highlighter: MapPoint => Any,
  ): MovementLayer = {
    var currentLocation = initialLocation
    val movingUnitTile = Tiles.newBuilder
        .withBackgroundColor(ANSITileColor.BRIGHT_CYAN.toData.multiplyAlphaBy(0.5))
        .build
    layer.setTileAt(converter.toRelativePosition(currentLocation).get, movingUnitTile)
    val movementSubscriptions = keyboardEvents
        .filter(MovementKeys.contains(_))
        .map {
          case 'W' | 'K' => Direction.Up
          case 'A' | 'H' => Direction.Left
          case 'S' | 'J' => Direction.Down
          case 'D' | 'L' => Direction.Right
        }
        // (_) is necessary in order to get the current variable value.
        .oMap(currentLocation.go(_).optFilter(converter.isInBounds))
        .subscribe {newLocation =>
          currentLocation = newLocation
          highlighter(currentLocation)
          layer.clear()
          layer.setTileAt(converter.toRelativePosition(currentLocation).get, movingUnitTile)
        }
    val openMenuSubscription =
      keyboardEvents.collect({
        case ' ' => OpenActionMenu
        case 'M' => Move
        // (_) is necessary in order to get the current variable value.
      }).subscribe(listener(currentLocation)(_))
    new MovementLayer(layer, movementSubscriptions, openMenuSubscription)
  }

  private val MovementKeys = "WASDHJKL"
}

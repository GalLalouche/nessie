package com.nessie.view.zirconview.input

import com.nessie.common.PromiseZ
import com.nessie.gm.{Movement, TurnAction}
import com.nessie.gm.TurnAction.MovementAction
import com.nessie.model.map.Direction
import com.nessie.view.zirconview.{Instructions, InstructionsPanel, MapGridPoint, ZirconMap}
import com.nessie.view.zirconview.input.MovementPlayerInput._
import com.nessie.view.zirconview.ZirconUtils._
import common.rich.func.{MoreObservableInstances, ToMoreMonadPlusOps}
import org.hexworks.zircon.api.color.ANSITileColor
import org.hexworks.zircon.api.graphics.Layer
import org.hexworks.zircon.api.screen.Screen

private class MovementPlayerInput(
    screen: Screen,
    mapGrid: ZirconMap,
    instructionsPanel: InstructionsPanel,
    promise: PromiseZ[TurnAction],
    initialLocation: MapGridPoint,
) extends ToMoreMonadPlusOps with MoreObservableInstances {
  private var isActive: Boolean = false
  private def isCurrentlyActive: Boolean = isActive

  private def createWasdLayer(): Layer = {
    var currentLocation = initialLocation
    val movingUnitTile = mapGrid.tileAt(currentLocation.mapPoint).createCopy
        .withBackgroundColor(ANSITileColor.BRIGHT_CYAN)
        .withForegroundColor(ANSITileColor.BRIGHT_MAGENTA)
    val $ = mapGrid.buildLayer
    $.setTileAt(currentLocation.relativePosition, movingUnitTile)
    val onActive = screen.keyboardActions.filter(_ => isCurrentlyActive)
    onActive
        .filter(MovementKeys contains _.getCharacter)
        .map(_.getCharacter match {
          case 'w' => Direction.Up
          case 'a' => Direction.Left
          case 's' => Direction.Down
          case 'd' => Direction.Right
        })
        // (_) is necessary in order to get the current variable value.
        .oMap(currentLocation.go(_))
        .foreach {newLocation =>
          currentLocation = newLocation
          $.clear()
          $.setTileAt(currentLocation.relativePosition, movingUnitTile)
        }
    onActive
        .filter(_.getCharacter == ' ')
        .foreach(_ => {
          // TODO check valid move location
          if (currentLocation != initialLocation)
            promise.fulfill(MovementAction(Movement(initialLocation.mapPoint, currentLocation.mapPoint)))
        })
    $
  }
  def toggleActive(): Unit = synchronized {
    isActive = !isActive
    if (isActive) {
      instructionsPanel.push(Instructions.Movement)
      screen.pushLayer(createWasdLayer())
    } else {
      instructionsPanel.popInstructions()
      screen.popLayer()
    }
  }
}

private object MovementPlayerInput {
  private val MovementKeys: String = "wasd"
}

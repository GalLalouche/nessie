package com.nessie.view.zirconview.input

import com.nessie.common.PromiseZ
import com.nessie.gm.{GameState, TurnAction}
import com.nessie.model.map.CombatUnitObject
import com.nessie.model.units.CombatUnit
import com.nessie.model.units.abilities.CanBeUsed
import com.nessie.view.zirconview.{Instructions, InstructionsPanel, ZirconMap}
import com.nessie.view.zirconview.ZirconUtils._
import common.rich.func.{MoreObservableInstances, ToMoreMonadPlusOps}
import org.hexworks.zircon.api.screen.Screen

import scalaz.concurrent.Task

private[zirconview] class ZirconPlayerInput(
    screen: Screen,
    mapGrid: ZirconMap,
    instructionsPanel: InstructionsPanel,
    screenDrawer: () => Unit,
) extends ToMoreMonadPlusOps with MoreObservableInstances {
  def nextState(currentlyPlayingUnit: CombatUnit, gs: GameState): Task[TurnAction] = {
    val promise = PromiseZ[TurnAction]()
    val location = CombatUnitObject.findIn(currentlyPlayingUnit, gs.map).get
    val movementPlayerInput: MovementPlayerInput = new MovementPlayerInput(
      screen = screen,
      mapGrid = mapGrid,
      instructionsPanel = instructionsPanel,
      promise = promise,
      initialLocation = mapGrid.toMapGridPoint(location),
    )
    instructionsPanel.push(Instructions.BasicInput)
    val movableLocations =
      CanBeUsed.getUsablePoints(currentlyPlayingUnit.moveAbility)(gs.map, location).toSet
    mapGrid.highlightMovable(movableLocations)
    screenDrawer()
    screen.simpleKeyStrokes().filter(_ == 'M').foreach(_ => movementPlayerInput.toggleActive())
    promise.toTask
  }
}

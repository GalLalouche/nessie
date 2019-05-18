package com.nessie.view.zirconview.input

import com.nessie.common.PromiseZ
import com.nessie.gm.{GameState, TurnAction}
import com.nessie.model.map.CombatUnitObject
import com.nessie.model.units.CombatUnit
import com.nessie.model.units.abilities.CanBeUsed
import com.nessie.view.zirconview.{Instructions, InstructionsPanel, ZirconMap}
import com.nessie.view.zirconview.ZirconUtils._
import common.rich.func.{MoreObservableInstances, ToMoreMonadPlusOps}
import org.hexworks.zircon.api.graphics.Layer
import org.hexworks.zircon.api.screen.Screen

import scalaz.{-\/, \/-}
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
    val popupMenu = new PopupMenu(mapGrid, gs, location, screen)
    def consumeMenuAction(a: MenuAction): Unit = a match {
      case MenuAction.Cancelled => ()
      case MenuAction.Action(a) => promise.fulfill(a)
    }
    val wasdLayer: Layer = WasdLayer.create(
      screen.simpleKeyStrokes(),
      mapGrid = mapGrid,
      observer = popupMenu.openMenu(_).unsafePerformAsync {
        case -\/(a) => ???
        case \/-(b) => consumeMenuAction(b)
      },
      initialLocation = mapGrid.toMapGridPoint(location),
    )
    instructionsPanel.push(Instructions.BasicInput)
    screen.pushLayer(wasdLayer)
    val movableLocations =
      CanBeUsed.getUsablePoints(currentlyPlayingUnit.moveAbility)(gs.map, location).toSet
    mapGrid.highlightMovable(movableLocations)
    screenDrawer()
    promise.toTask
  }
}
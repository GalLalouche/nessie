package com.nessie.view.zirconview.input

import com.nessie.common.PromiseZ
import com.nessie.gm.{GameState, TurnAction}
import com.nessie.model.map.CombatUnitObject
import com.nessie.model.units.CombatUnit
import com.nessie.model.units.abilities.CanBeUsed
import com.nessie.view.zirconview.{Instructions, InstructionsPanel, ZirconMap}
import com.nessie.view.zirconview.ZirconUtils._
import common.rich.RichT._
import common.rich.func.{MoreObservableInstances, ToMoreFunctorOps, ToMoreMonadPlusOps}
import org.hexworks.zircon.api.screen.Screen
import org.hexworks.zircon.api.uievent.KeyCode

import scalaz.{-\/, \/-}
import scalaz.concurrent.Task

private[zirconview] class ZirconPlayerInput(
    screen: Screen,
    mapGrid: ZirconMap,
    instructionsPanel: InstructionsPanel,
    screenDrawer: () => Unit,
) extends ToMoreMonadPlusOps with MoreObservableInstances
    with ToMoreFunctorOps {
  // TODO create a single-time consumable data structure: can add and fold, which clears
  def nextState(currentlyPlayingUnit: CombatUnit, gs: GameState): Task[TurnAction] = {
    val promise = PromiseZ[TurnAction]()
    val location = CombatUnitObject.findIn(currentlyPlayingUnit, gs.map).get
    val popupMenu = new PopupMenu(mapGrid, gs, location)
    val endTurnSub = screen.keyCodes().filter(_ == KeyCode.ENTER).head.subscribe {_ =>
      promise fulfill TurnAction.EndTurn
    }
    def consumeMenuAction(a: MenuAction): Unit = a match {
      case MenuAction.Cancelled => ()
      case MenuAction.Action(a) =>
        promise.fulfill(a)
    }
    val wasdLayer: WasdLayer = WasdLayer.create(
      screen.simpleKeyStrokes(),
      mapGrid = mapGrid,
      observer = popupMenu.openMenu(_).|>(screen.modalTask).unsafePerformAsync {
        case -\/(a) => ???
        case \/-(b) => consumeMenuAction(b)
      },
      initialLocation = mapGrid.toMapGridPoint(location),
    )
    instructionsPanel.push(Instructions.BasicInput)
    screen.pushLayer(wasdLayer.layer)
    val movableLocations =
      CanBeUsed.getUsablePoints(currentlyPlayingUnit.moveAbility)(gs.map, location).toSet
    mapGrid.highlightMovable(movableLocations)
    screenDrawer()
    promise.toTask.listen {_ =>
      screen.popLayer()
      endTurnSub.unsubscribe()
      wasdLayer.clear()
    }
  }
}

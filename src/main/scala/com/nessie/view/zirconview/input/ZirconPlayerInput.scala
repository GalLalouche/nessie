package com.nessie.view.zirconview.input

import com.nessie.common.PromiseZ
import com.nessie.gm.{GameState, TurnAction}
import com.nessie.model.map.CombatUnitObject
import com.nessie.model.units.CombatUnit
import com.nessie.model.units.abilities.CanBeUsed
import com.nessie.view.zirconview.ZirconUtils._
import com.nessie.view.zirconview.{Instructions, InstructionsPanel, MapPointHighlighter, ZirconMap}
import common.rich.RichT._
import common.rich.func.{MoreObservableInstances, ToMoreFunctorOps, ToMoreMonadPlusOps}
import org.hexworks.zircon.api.screen.Screen
import org.hexworks.zircon.api.uievent.KeyCode
import scalaz.concurrent.Task
import scalaz.{-\/, \/-}

private[zirconview] class ZirconPlayerInput(
    screen: Screen,
    mapGrid: ZirconMap,
    instructionsPanel: InstructionsPanel,
    screenDrawer: () => Unit,
    highlighter: MapPointHighlighter,
) extends ToMoreMonadPlusOps with MoreObservableInstances
    with ToMoreFunctorOps {
  def nextState(currentlyPlayingUnit: CombatUnit, gs: GameState): Task[TurnAction] = {
    val promise = PromiseZ[TurnAction]()
    val location = CombatUnitObject.findIn(currentlyPlayingUnit, gs.map).get
    val popupMenu = new PopupMenu(mapGrid, gs, location)
    val endTurnSub = screen.keyCodes().filter(_ == KeyCode.ENTER).head.subscribe {_ =>
      promise fulfill TurnAction.EndTurn
    }
    def consumeMenuAction(a: MenuAction): Unit = a match {
      case MenuAction.Cancelled => ()
      case MenuAction.Action(a) => promise.fulfill(a)
    }
    val movementLayer: MovementLayer = MovementLayer.create(
      keyboardEvents = screen.simpleKeyStrokes(),
      layer = mapGrid.buildLayer,
      menuOpener = popupMenu.openMenu(_).|>(screen.modalTask).unsafePerformAsync {
        case -\/(_) => ???
        case \/-(b) => consumeMenuAction(b)
      },
      initialLocation = mapGrid.toMapGridPoint(location),
      highlighter = highlighter(gs, _)
    )
    instructionsPanel.push(Instructions.BasicInput)
    screen.pushLayer(movementLayer.layer)
    mapGrid.highlightMovable(
      CanBeUsed.getUsablePoints(gs.currentTurn.get.remainingMovementAbility)(gs.map, location))
    screenDrawer()
    promise.toTask.listen {_ =>
      screen.popLayer()
      endTurnSub.unsubscribe()
      movementLayer.clear()
    }
  }
}

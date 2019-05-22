package com.nessie.view.zirconview.input

import com.nessie.common.PromiseZ
import com.nessie.gm.{GameState, TurnAction}
import com.nessie.model.map.{CombatUnitObject, MapPoint}
import com.nessie.model.units.CombatUnit
import com.nessie.model.units.abilities.{AbilityToTurnAction, CanBeUsed}
import com.nessie.view.zirconview.{Instructions, InstructionsPanel, MapPointHighlighter, ZirconMap}
import com.nessie.view.zirconview.ZirconUtils._
import com.nessie.view.zirconview.input.MovementLayer.MovementLayerAction
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
    val remainingMoveAbility = gs.currentTurn.get.remainingMovementAbility
    def consumeMenuAction(a: MenuAction): Unit = a match {
      case MenuAction.Cancelled => ()
      case MenuAction.Action(a) => promise.fulfill(a)
    }
    def listenToMovementLayer(mp: MapPoint): MovementLayerAction => Unit = {
      case MovementLayer.OpenActionMenu => popupMenu.openMenu(mp).|>(screen.modalTask).unsafePerformAsync {
        case -\/(_) => ???
        case \/-(b) => consumeMenuAction(b)
      }
      case MovementLayer.Move =>
        if (CanBeUsed(remainingMoveAbility)(gs.map, location, mp))
          promise.fulfill(AbilityToTurnAction(remainingMoveAbility)(location, mp))
    }
    val movementLayer: MovementLayer = MovementLayer.create(
      keyboardEvents = screen.simpleKeyStrokes(),
      layer = mapGrid.buildLayer,
      listener = listenToMovementLayer,
      initialLocation = mapGrid.toMapGridPoint(location),
      highlighter = highlighter(gs, _)
    )
    instructionsPanel.push(Instructions.BasicInput)
    screen.pushLayer(movementLayer.layer)
    mapGrid.highlightMovable(
      CanBeUsed.getUsablePoints(remainingMoveAbility)(gs.map, location))
    screenDrawer()
    promise.toTask.listen {_ =>
      screen.popLayer()
      endTurnSub.unsubscribe()
      movementLayer.clear()
    }
  }
}

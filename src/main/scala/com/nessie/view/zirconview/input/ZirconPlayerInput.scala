package com.nessie.view.zirconview.input

import com.nessie.common.PromiseZ
import com.nessie.gm.{GameState, TurnAction}
import com.nessie.model.map.{CombatUnitObject, MapPoint}
import com.nessie.model.units.CombatUnit
import com.nessie.model.units.abilities.{AbilityToTurnAction, CanBeUsed}
import com.nessie.view.zirconview.{Instructions, InstructionsPanel, MapPointHighlighter}
import com.nessie.view.zirconview.ZirconUtils._
import com.nessie.view.zirconview.input.MovementLayer.MovementLayerAction
import com.nessie.view.zirconview.map.ZirconMap
import com.nessie.view.zirconview.screen.ZirconScreen
import org.hexworks.zircon.api.uievent.KeyCode

import scalaz.{-\/, \/-}
import scalaz.concurrent.Task
import common.rich.func.ToMoreFunctorOps._

import common.rich.RichT._

private[zirconview] class ZirconPlayerInput(
    screen: ZirconScreen,
    mapGrid: ZirconMap,
    instructionsPanel: InstructionsPanel,
    screenDrawer: () => Unit,
    highlighter: MapPointHighlighter,
) {
  def nextState(currentlyPlayingUnit: CombatUnit, gs: GameState): Task[TurnAction] = {
    val promise = PromiseZ[TurnAction]()
    val location = CombatUnitObject.findIn(currentlyPlayingUnit, gs.map).get
    mapGrid.center(location)
    val converter = mapGrid.mapPointConverter
    val popupMenu = new PopupMenu(converter, gs, location)
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
      initialLocation = location,
      converter = converter,
      highlighter = highlighter(gs, _)
    )
    instructionsPanel.push(Instructions.BasicInput)
    screen.screen.pushLayer(movementLayer.layer)
    mapGrid.highlightMovable(
      CanBeUsed.getUsablePoints(remainingMoveAbility)(gs.map, location))
    screenDrawer()
    promise.toTask.listen {_ =>
      screen.screen.popLayer()
      endTurnSub.unsubscribe()
      movementLayer.clear()
    }
  }
}

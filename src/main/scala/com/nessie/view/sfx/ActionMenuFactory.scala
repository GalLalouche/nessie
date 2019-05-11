package com.nessie.view.sfx

import com.nessie.gm.{GameState, TurnAction}
import com.nessie.gm.TurnAction.EndTurn
import com.nessie.model.map.{CombatUnitObject, MapPoint}
import com.nessie.model.units.abilities.{AbilityToTurnAction, CanBeUsed, MoveAbility, UnitAbility}
import common.rich.primitives.RichBoolean._
import rx.lang.scala.Observer
import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.scene.control.{ContextMenu, MenuItem}
import scalafx.scene.control.MenuItem._

private class ActionMenuFactory(
    source: MapPoint, gs: GameState, observer: Observer[TurnAction]
) {
  // FIXME this should check the currentTurn state to change the movement range etc.
  def apply(destination: MapPoint): ContextMenu = {
    val $ = new ContextMenu()
    val currentTurn = gs.currentTurn.get
    def toItem(unitAbility: UnitAbility): MenuItem =
      new MenuItem(unitAbility.name) {
        // TODO extract this check elsewhere, something similar to CanBeUsed
        disable = CanBeUsed.negate(unitAbility)(gs.map, source, destination) ||
            (unitAbility.isInstanceOf[MoveAbility] && currentTurn.remainingMovement == 0 ||
                currentTurn.canAppendAction.isFalse)
        onAction = (_: ActionEvent) => {
          $.hide()
          observer onNext AbilityToTurnAction(unitAbility)(source, destination)
        }
      }
    gs.map(source).asInstanceOf[CombatUnitObject].unit.abilities
        .map(toItem)
        .foreach($.items.+=(_))
    $.items += new MenuItem("Cancel") {onAction = (_: ActionEvent) => $.hide()}
    $.items += new MenuItem("End turn") {
      onAction = (_: ActionEvent) => {
        $.hide()
        observer onNext EndTurn
      }
    }
    $.style = Styles.baseColor("white")
    $
  }
}

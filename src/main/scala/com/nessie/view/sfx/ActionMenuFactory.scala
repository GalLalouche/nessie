package com.nessie.view.sfx

import com.nessie.common.sfx.Styles
import com.nessie.gm.{GameState, TurnAction}
import com.nessie.gm.TurnAction.EndTurn
import com.nessie.model.map.MapPoint
import com.nessie.model.units.abilities.{AbilityToTurnAction, UnitAbility}
import com.nessie.view.ActionMenuHelper
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
    def toItem(unitAbility: UnitAbility, disabled: Boolean): MenuItem =
      new MenuItem() {
        disable = disabled
        onAction = {_ =>
          $.hide()
          observer onNext AbilityToTurnAction(unitAbility)(source, destination)
        }
      }
    ActionMenuHelper.usableAbilities(gs)(src = source, dst = destination)
        .map((toItem _).tupled)
        .foreach($.items.+=(_))
    $.items += new MenuItem("Cancel") {onAction = (_: ActionEvent) => $.hide()}
    $.items += new MenuItem("End turn") {
      onAction = {_ =>
        $.hide()
        observer onNext EndTurn
      }
    }
    $.style = Styles.baseColor("white")
    $
  }
}

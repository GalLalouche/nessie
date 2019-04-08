package com.nessie.view.sfx

import com.nessie.gm.GameState
import com.nessie.model.map.{CombatUnitObject, MapPoint}
import com.nessie.model.units.abilities.UnitAbility
import rx.lang.scala.Observer

import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.scene.control.MenuItem._
import scalafx.scene.control.{ContextMenu, MenuItem}

private class ActionMenuFactory(source: MapPoint, gs: GameState, pubSubManager: Observer[GameState]) {
  def apply(destination: MapPoint): ContextMenu = {
    val $ = new ContextMenu()
    def toItem(unitAbility: UnitAbility): MenuItem =
      new MenuItem(unitAbility.name) {
        disable = !unitAbility.canBeUsed(gs.map, source, destination)
        onAction = (_: ActionEvent) => {
          $.hide()
          pubSubManager onNext unitAbility.applyTo(source, destination)(gs)
        }
      }
    gs.map(source).asInstanceOf[CombatUnitObject].unit.abilities
        .map(toItem)
        .foreach($.items.+=(_))
    $.items += new MenuItem("Cancel") { onAction = (_: ActionEvent) => $.hide() }
    $.style = Styles.baseColor("white")
    $
  }
}

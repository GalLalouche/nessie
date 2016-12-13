package com.nessie.map.view.sfx

import com.nessie.gm.GameState
import com.nessie.map.CombatUnitObject
import com.nessie.map.model.MapPoint
import com.nessie.units.abilities.UnitAbility
import rx.lang.scala.Observer

import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.scene.control.MenuItem._
import scalafx.scene.control.{ContextMenu, MenuItem}

private class ActionMenuFactory(source: MapPoint, gs: GameState, pubSubManager: Observer[GameState]) {
  private val map = gs.map
  def apply(destination: MapPoint): ContextMenu = {
    val menu = new ContextMenu()
    def toItem(unitAbility: UnitAbility): MenuItem =
      new MenuItem(unitAbility.name) {
        disable = !unitAbility.canBeUsed(gs.map, source, destination)
        onAction = (e: ActionEvent) => {
          menu.hide()
          pubSubManager onNext unitAbility.applyTo(source, destination)(gs)
        }
      }
    map(source).asInstanceOf[CombatUnitObject].unit.abilities
        .map(toItem)
        .foreach(menu.items.+=(_))
    menu.items += new MenuItem("Cancel") { onAction = (e: ActionEvent) => menu.hide() }
    menu
  }
}

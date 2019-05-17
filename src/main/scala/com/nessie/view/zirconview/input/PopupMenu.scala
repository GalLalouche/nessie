package com.nessie.view.zirconview.input

import com.nessie.gm.GameState
import com.nessie.model.map.MapPoint
import com.nessie.model.units.abilities.AbilityToTurnAction
import com.nessie.view.zirconview.{ModalResultWrapper, OnBuildWrapper, ZirconConstants, ZirconMap}
import com.nessie.view.zirconview.input.PopupMenu._
import com.nessie.view.zirconview.ZirconUtils._
import com.nessie.view.ActionMenuHelper
import org.hexworks.zircon.api.{Components, Positions, Sizes}
import org.hexworks.zircon.api.builder.component.ModalBuilder
import org.hexworks.zircon.api.color.ANSITileColor
import org.hexworks.zircon.api.component.{ColorTheme, Panel}
import org.hexworks.zircon.api.component.modal.Modal
import org.hexworks.zircon.api.screen.Screen
import org.hexworks.zircon.api.uievent.KeyCode

import scalaz.concurrent.Task

private class PopupMenu(mapGrid: ZirconMap, gs: GameState, source: MapPoint, screen: Screen) {
  def openMenu(destination: MapPoint): Task[MenuAction] = {
    val panel: Panel = Components.panel()
        .withTitle("Actions")
        .withSize(20, 10)
        .withPosition(Positions.zero())
        .wrapWithBox(true)
        .wrapWithShadow(true)
        .build()
    val modal: Modal[ModalResultWrapper[MenuAction]] = ModalBuilder
        .newBuilder()
        .withParentSize(panel.getSize.plus(Sizes.create(1, 1)))
        .withPosition(mapGrid.toMapGridPoint(destination).absolutePosition)
        .withComponent(panel)
        .build()
    panel.addComponents(
      ActionMenuHelper.usableAbilities(gs)(source, destination).map {
        case (ua, disabled) =>
          val $ = Components.button.withText(ua.name)
          OnBuildWrapper($)(b => {
            if (disabled) {
              b.applyColorTheme(DisabledTheme)
            } else
              b.applyColorTheme(EnabledTheme)
            b.onActivation(() => modal.close(ModalResultWrapper(MenuAction.Action(
              AbilityToTurnAction(ua)(src = source, dst = destination)))))
          })
      }
    )

    modal.keyCodes().filter(_ == KeyCode.ESCAPE).foreach {
      _ => modal.close(ModalResultWrapper(MenuAction.Cancelled))
    }
    screen.openModalTask(modal)
  }
}

object PopupMenu {
  private val EnabledTheme: ColorTheme = ZirconConstants.Theme
  private val DisabledTheme: ColorTheme = EnabledTheme.toData.copy(getAccentColor = ANSITileColor.GRAY)
}

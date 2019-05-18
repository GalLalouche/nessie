package com.nessie.view.zirconview.input

import com.nessie.gm.GameState
import com.nessie.model.map.MapPoint
import com.nessie.model.units.abilities.AbilityToTurnAction
import com.nessie.view.zirconview.{MapPointConverter, OnBuildWrapper, ZirconConstants}
import com.nessie.view.zirconview.input.PopupMenu._
import com.nessie.view.zirconview.ZirconUtils._
import com.nessie.view.ActionMenuHelper
import org.hexworks.zircon.api.{Components, Positions, Sizes}
import org.hexworks.zircon.api.builder.component.ModalBuilder
import org.hexworks.zircon.api.color.ANSITileColor
import org.hexworks.zircon.api.component.{ColorTheme, Panel}
import org.hexworks.zircon.api.uievent.KeyCode

private class PopupMenu(mpc: MapPointConverter, gs: GameState, source: MapPoint) {
  def openMenu(destination: MapPoint): WrappedModal[MenuAction] = {
    val panel: Panel = Components.panel
        .withTitle("Actions")
        .withSize(20, 10)
        .withPosition(Positions.zero)
        .wrapWithBox(true)
        .wrapWithShadow(true)
        .build
    val $: WrappedModal[MenuAction] = ModalBuilder
        .newBuilder()
        .withParentSize(panel.getSize.plus(Sizes.create(1, 1)))
        .withPosition(mpc.toAbsolutePosition(destination))
        .withComponent(panel)
        .build()
    panel.addComponents(
      ActionMenuHelper.usableAbilities(gs)(source, destination).map {
        case (ua, disabled) =>
          OnBuildWrapper(Components.button.withText(ua.name))(b => {
            if (disabled) {
              b.disable()
              b.applyColorTheme(DisabledTheme)
            } else
              b.applyColorTheme(EnabledTheme)
            b.onActivation(() => $.close(MenuAction.Action(
              AbilityToTurnAction(ua)(src = source, dst = destination))))
          })
      }
    )

    $.keyCodes().filter(_ == KeyCode.ESCAPE).foreach {
      _ => $.close(MenuAction.Cancelled)
    }
    $
  }
}

object PopupMenu {
  private val EnabledTheme: ColorTheme = ZirconConstants.Theme
  private val DisabledTheme: ColorTheme = EnabledTheme.toData.copy(getAccentColor = ANSITileColor.GRAY)
}

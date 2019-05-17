package com.nessie.view.zirconview

import common.rich.RichObservable
import common.rich.RichT._
import monocle.Lens
import org.hexworks.zircon.api.component.CheckBox
import org.hexworks.zircon.api.data.{Position, Tile}
import org.hexworks.zircon.api.graphics.DrawSurface
import org.hexworks.zircon.api.uievent.{KeyboardEvent, KeyboardEventType, KeyCode, MouseEvent, MouseEventType, Processed, UIEventPhase, UIEventSource}
import rx.lang.scala.Observable

private object ZirconUtils {
  implicit class RichPosition(private val $: Position) extends AnyVal {
    def withInverseRelative(other: Position): Position =
      $.withRelativeX(-other.getX).withRelativeY(-other.getY)
  }

  def tileLens(p: Position): Lens[DrawSurface, Tile] =
    Lens[DrawSurface, Tile](_.getTileAt(p).get)(t => _.applyAndReturn(_.setTileAt(p, t)))

  implicit class RichUIEventSource(private val $: UIEventSource) extends AnyVal {
    def mouseActions(t: MouseEventType): Observable[MouseEvent] =
      RichObservable.register(f => $.onMouseEvent(t, (mouseEvent: MouseEvent, uiEventPhase: UIEventPhase) => {
        if (uiEventPhase == UIEventPhase.TARGET)
          f(mouseEvent)
        Processed.INSTANCE
      }))
    def mouseActions(): MouseEvents = RichObservable.concat(MouseEventType.values.map(mouseActions))
    def mouseClicks(): MouseEvents = mouseActions(MouseEventType.MOUSE_CLICKED)

    def keyboardActions(t: KeyboardEventType = KeyboardEventType.KEY_PRESSED): KeyboardEvents =
      RichObservable.register(f =>
        $.onKeyboardEvent(t, (keyboardEvent: KeyboardEvent, uiEventPhase: UIEventPhase) => {
          if (uiEventPhase == UIEventPhase.TARGET)
            f(keyboardEvent)
          Processed.INSTANCE
        }))
    def keyCodes(): KeyCodes = keyboardActions().map(_.getCode)
    def simpleKeyStrokes(): SimpleKeyboardEvents = keyboardActions().map(_.getCode.getChar)
  }

  implicit class RichCheckBox(private val $: CheckBox) extends AnyVal {
    // Nice API there buddy!
    def isChecked: Boolean = $.getState ==
        org.hexworks.zircon.internal.component.impl.DefaultCheckBox.CheckBoxState.CHECKED
  }

  implicit class RichKeyCode(private val $: KeyCode) extends AnyVal {
    def getChar: Char = $.toChar.get.toChar
  }
  implicit class RichColorTheme(private val $: ColorTheme) extends AnyVal {
    def toData: ColorThemeData = ColorThemeData.from($)
  }
}

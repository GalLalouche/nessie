package com.nessie.view.zirconview

import com.nessie.common.PromiseZ
import common.rich.RichObservable
import common.rich.collections.RichIterator._
import common.rich.RichT._
import common.rich.func.{MoreObservableInstances, ToMoreFoldableOps, ToMoreMonadPlusOps}
import monocle.Lens
import org.hexworks.cobalt.datatypes.Maybe
import org.hexworks.zircon.api.component.{CheckBox, ColorTheme, Component, Container}
import org.hexworks.zircon.api.component.modal.Modal
import org.hexworks.zircon.api.data.{Position, Tile}
import org.hexworks.zircon.api.graphics.DrawSurface
import org.hexworks.zircon.api.screen.Screen
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.uievent.{ComponentEvent, ComponentEventType, KeyboardEvent, KeyboardEventType, KeyCode, MouseEvent, MouseEventType, Processed, UIEventPhase, UIEventSource}
import rx.lang.scala.Observable

import scala.collection.JavaConverters._

import scalaz.concurrent.Task
import scalaz.std.OptionInstances

private object ZirconUtils
    extends ToMoreFoldableOps with OptionInstances
        with ToMoreMonadPlusOps with MoreObservableInstances {
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
    def simpleKeyStrokes(): SimpleKeyboardEvents = keyboardActions().oMap(_.getCode.getOChar)
  }

  implicit class RichCheckBox(private val $: CheckBox) extends AnyVal {
    // Nice API there buddy!
    def isChecked: Boolean = $.getState ==
        org.hexworks.zircon.internal.component.impl.DefaultCheckBox.CheckBoxState.CHECKED
  }

  implicit class RichMaybe[A](private val $: Maybe[A]) extends AnyVal {
    def toOption: Option[A] = if ($.isEmpty) None else Option($.get)
  }
  implicit class RichKeyCode(private val $: KeyCode) extends AnyVal {
    def getChar: Char = $.toChar.get.toChar
    def getOChar: Option[Char] = $.toChar.toOption.map(_.toChar)
  }

  implicit class RichComponent(private val $: Component) extends AnyVal {
    def onActivation(f: () => Any): Unit =
      $.onComponentEvent(ComponentEventType.ACTIVATED, (_: ComponentEvent) => {
        f()
        Processed.INSTANCE
      })
  }

  implicit class RichContainer(private val $: Container) extends AnyVal {
    def addComponents(
        bps: Seq[OnBuildWrapper[_ <: Component, _]],
        initialPosition: Position = Positions.create(0, 1),
        relativePosition: Position = Positions.create(-1, -1),
    ): Unit = {
      bps.foreach {obBuildWrapper =>
        val position = $.getChildren
            .iterator.asScala
            .lastOption
            .mapHeadOrElse(relativePosition.relativeToBottomOf, initialPosition)
        obBuildWrapper.cb.withPosition(position)
        $.addComponent(obBuildWrapper.build())
      }
    }
  }

  implicit class RichScreen(private val $: Screen) extends AnyVal {
    def openModalTask[A](m: Modal[ModalResultWrapper[A]]): Task[A] = {
      val promise = PromiseZ[A]()
      m.onClosed(promise fulfill _.value)
      $.openModal(m)
      promise.toTask
    }
  }

  implicit class RichModal[A](private val $: Modal[ModalResultWrapper[A]]) extends AnyVal {
    def close(result: A): Unit = $.close(ModalResultWrapper(result))
  }

  implicit class RichColorTheme(private val $: ColorTheme) extends AnyVal {
    def toData: ColorThemeData = ColorThemeData.from($)
  }
}

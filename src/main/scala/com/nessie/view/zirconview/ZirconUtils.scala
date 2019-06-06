package com.nessie.view.zirconview

import com.nessie.model.map.GridSize
import common.rich.RichObservable
import common.rich.RichObservable.Unsubscribable
import common.rich.RichT._
import common.rich.collections.RichIterator._
import common.rich.func.{MoreObservableInstances, ToMoreFoldableOps, ToMoreMonadPlusOps}
import monocle.Optional
import org.hexworks.cobalt.datatypes.Maybe
import org.hexworks.cobalt.events.api.{CancelledByHand, Subscription}
import org.hexworks.zircon.api.{Positions, Sizes}
import org.hexworks.zircon.api.behavior.{Boundable, Disablable}
import org.hexworks.zircon.api.color.TileColor
import org.hexworks.zircon.api.component.{CheckBox, ColorTheme, Component, Container}
import org.hexworks.zircon.api.component.modal.Modal
import org.hexworks.zircon.api.data.{Position, Rect, Size, Tile}
import org.hexworks.zircon.api.graphics.{DrawSurface, Layer, TileComposite}
import org.hexworks.zircon.api.screen.Screen
import org.hexworks.zircon.api.uievent._
import rx.lang.scala.Observable

import scala.collection.JavaConverters._

import scalaz.std.OptionInstances

private object ZirconUtils
    extends ToMoreFoldableOps with OptionInstances
        with ToMoreMonadPlusOps with MoreObservableInstances {
  implicit class RichGridSize(private val $: GridSize) extends AnyVal {
    def toZirconSize: Size = Sizes.create($.width, $.height)
  }
  implicit class RichSize(private val $: Size) extends AnyVal {
    @inline def width: Int = $.getWidth
    @inline def height: Int = $.getHeight
  }
  implicit class RichTileComposite(private val $: TileComposite) extends AnyVal {
    @inline def width: Int = $.getWidth
    @inline def height: Int = $.getWidth
    @inline def size: Size = $.getSize
  }
  implicit class RichPosition(private val $: Position) extends AnyVal {
    def withInverseRelative(other: Position): Position =
      $.withRelativeX(-other.getX).withRelativeY(-other.getY)
    @inline def x: Int = $.getX
    @inline def y: Int = $.getY
    def inSizedContainer(p: Position, size: Size): Boolean =
      $.x >= p.x & $.y >= p.y && $.x < p.x + size.width && $.y < p.y + size.height
  }

  def tileLens(p: Position): Optional[DrawSurface, Tile] =
    Optional[DrawSurface, Tile](_.getTileAt(p).toOption)(t => _.applyAndReturn(_.setTileAt(p, t)))

  private implicit val UnsubscribableZircon: Unsubscribable[Subscription] = _.cancel(CancelledByHand.INSTANCE)
  private def handleEvent[A](f: A => Any)(a: A, p: UIEventPhase): UIEventResponse = {
    if (p == UIEventPhase.TARGET)
      f(a)
    Processed.INSTANCE
  }
  implicit class RichUIEventSource(private val $: UIEventSource) extends AnyVal {
    def mouseActions(t: MouseEventType): Observable[MouseEvent] =
      RichObservable.registerUnsubscribable(f => $.onMouseEvent(t, handleEvent(f)))
    def mouseActions(): MouseEvents = RichObservable.concat(MouseEventType.values.map(mouseActions))
    def mouseClicks(): MouseEvents = mouseActions(MouseEventType.MOUSE_CLICKED)

    def keyboardActions(t: KeyboardEventType = KeyboardEventType.KEY_PRESSED): KeyboardEvents =
      RichObservable.registerUnsubscribable(f => $.onKeyboardEvent(t, handleEvent(f)))
    def keyCodes(): KeyCodes = keyboardActions().map(_.getCode)
    def simpleKeyStrokes(): SimpleKeyboardEvents = keyboardActions().oMap(_.getCode.getOChar)
  }

  implicit class RichCheckBox(private val $: CheckBox) extends AnyVal {
    // Nice API there buddy!
    def isChecked: Boolean =
      $.getState == org.hexworks.zircon.internal.component.impl.DefaultCheckBox.CheckBoxState.CHECKED ||
          $.getState == org.hexworks.zircon.internal.component.impl.DefaultCheckBox.CheckBoxState.UNCHECKING
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
        if ($.safeAs[Disablable].mapHeadOrElse(_.isEnabled, true)) {
          f()
          Processed.INSTANCE
        } else
          Pass.INSTANCE
      })
  }

  implicit class RichDisablable(private val $: Disablable) extends AnyVal {
    def disable(): Unit = $.getDisabledProperty.setValue(true)
    def enable(): Unit = $.getDisabledProperty.setValue(false)
  }

  implicit class RichContainer(private val $: Container) extends AnyVal {
    def addComponents(
        bps: Seq[OnBuildWrapper[_ <: Component, _]],
        initialPosition: Position = Positions.create(0, 1),
        relativePosition: Position = Positions.create(-1, -1),
    ): Unit = bps.foreach {obBuildWrapper =>
      val position = $.getChildren
          .iterator.asScala
          .lastOption
          .mapHeadOrElse(relativePosition.relativeToBottomOf, initialPosition)
      obBuildWrapper.cb.withPosition(position)
      $.addComponent(obBuildWrapper.build())
    }

    def find(p: Component => Boolean): Option[Component] = $.getChildren.iterator.asScala.find(p)
    def collect[A](pf: PartialFunction[Component, A]): Iterator[A] =
      $.getChildren.iterator.asScala.collect(pf)
    def children: Seq[Component] = $.getChildren.toArray.toVector.map(_.asInstanceOf[Component])
  }

  implicit class RichScreen(private val $: Screen) extends AnyVal {
    def containsLayer(l: Layer): Boolean = $.getLayers.contains(l)
  }

  implicit class RichModal[A](private val $: Modal[ModalResultWrapper[A]]) extends AnyVal {
    def close(result: A): Unit = $.close(ModalResultWrapper(result))
  }

  implicit class RichLayer(private val $: Layer) extends AnyVal {
    def clearCopy: Layer = $.createCopy <| (_.clear())
  }

  implicit class RichBoundable(private val $: Boundable) extends AnyVal {
    def position: Position = $.getPosition
    def width: Int = $.getWidth
    def height: Int = $.getHeight
    def rect: Rect = $.getRect
    // TODO handle shadows
    def points: Set[Position] = ($ match {
      case _: Modal[_] => rect.withHeight(height - 2).withWidth(width - 2)
      case _ => rect
    }).fetchPositions.iterator.asScala.toSet
    def intersectingPoints(other: Boundable): Set[Position] = $.points.intersect(other.points)
  }

  implicit class RichColorTheme(private val $: ColorTheme) extends AnyVal {
    def toData: ColorThemeData = ColorThemeData.from($)
  }
  implicit class RichTileColor(private val $: TileColor) extends AnyVal {
    def toData: TileColorData = TileColorData.from($)
  }
}

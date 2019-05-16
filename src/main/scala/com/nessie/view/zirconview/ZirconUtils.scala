package com.nessie.view.zirconview

import com.nessie.model.map.{BattleMap, MapPoint}
import common.rich.RichT._
import monocle.Lens
import org.hexworks.zircon.api.data.{Position, Tile}
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.behavior.InputEmitter
import org.hexworks.zircon.api.graphics.DrawSurface
import org.hexworks.zircon.api.input.{KeyStroke, MouseAction}
import org.hexworks.zircon.api.listener.MouseListener
import rx.lang.scala.Observable
import rx.lang.scala.subjects.PublishSubject

private object ZirconUtils {
  implicit class RichPosition(private val $: Position) extends AnyVal {
    def withInverseRelative(other: Position): Position =
      $.withRelativeX(-other.getX).withRelativeY(-other.getY)
  }

  def tileLens(p: Position): Lens[DrawSurface, Tile] =
    Lens[DrawSurface, Tile](_.getTileAt(p).get)(t => _.applyAndReturn(_.setTileAt(p, t)))

  implicit class RichInputEmitter(private val $: InputEmitter) extends AnyVal {
    def mouseActions: Observable[MouseAction] = {
      val o = PublishSubject[MouseAction]()
      $.onMouseAction(new MouseListener {
        override def mouseClicked(mouseAction: MouseAction) = o.onNext(mouseAction)
        override def mouseDragged(mouseAction: MouseAction) = o.onNext(mouseAction)
        override def mouseEntered(mouseAction: MouseAction) = o.onNext(mouseAction)
        override def mouseExited(mouseAction: MouseAction) = o.onNext(mouseAction)
        override def mouseMoved(mouseAction: MouseAction) = o.onNext(mouseAction)
        override def mousePressed(mouseAction: MouseAction) = o.onNext(mouseAction)
        override def mouseReleased(mouseAction: MouseAction) = o.onNext(mouseAction)
        override def mouseWheelRotatedDown(mouseAction: MouseAction) = o.onNext(mouseAction)
        override def mouseWheelRotatedUp(mouseAction: MouseAction) = o.onNext(mouseAction)
      })
      o
    }
    def mouseClicks: Observable[MouseAction] = {
      val o = PublishSubject[MouseAction]()
      $.onMouseClicked(o.onNext)
      o
    }
    def keyboardActions: Observable[KeyStroke] = {
      val o = PublishSubject[KeyStroke]()
      $.onKeyStroke(o.onNext)
      o
    }
  }
}

package com.nessie.view.zirconview.screen

import com.nessie.common.PromiseZ
import com.nessie.model.map.fov.FogOfWar
import com.nessie.view.zirconview.{InstructionsPanel, MapPointHighlighter, ModalResultWrapper}
import com.nessie.view.zirconview.ZirconUtils._
import com.nessie.view.zirconview.map.ZirconMap
import common.rich.primitives.RichBoolean._
import org.hexworks.zircon.api.{TileColors, Tiles}
import org.hexworks.zircon.api.component.modal.Modal
import org.hexworks.zircon.api.data.{Position, Tile}
import org.hexworks.zircon.api.screen.Screen
import org.hexworks.zircon.api.uievent.{KeyboardEventHandler, KeyboardEventType, MouseEventHandler, MouseEventType}

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

private class ZirconScreenImpl(
    override val screen: Screen,
    propertiesPanel: PropertiesPanel,
    debugPanel: DebugButtonPanel,
    override val instructions: InstructionsPanel,
    val mapGridPosition: Position,
    override val map: ZirconMap,
) extends ZirconScreen {
  override val highlighter: MapPointHighlighter = propertiesPanel.highlighter
  override def updateMap(fow: FogOfWar): Unit = synchronized {
    map.update(fow)
    drawMap()
  }
  override def drawMap(): Unit = synchronized {
    screen.draw(map.graphics, mapGridPosition)
    val layer = map.fogOfWarLayer
    if (screen.getLayers.asScala.contains(layer).isFalse)
      screen.pushLayer(layer)
  }
  override def nextSmallStep(): Unit = debugPanel.nextSmallStep()
  screen.display()
  map.mouseEvents(screen)
      // This can fail if the event occurs before the map was updated
      .filter(_.exists(map.getCurrentMap.isInBounds))
      .foreach(mp => {
        propertiesPanel.update(map.getCurrentMap.map)(mp)
        if (debugPanel.isHoverFovChecked) {
          map.updateViewAndFog(mp)
          drawMap()
        }
      })

  override def onKeyboardEvent(ket: KeyboardEventType, keh: KeyboardEventHandler) =
    screen.onKeyboardEvent(ket, keh)
  override def onMouseEvent(met: MouseEventType, meh: MouseEventHandler) =
    screen.onMouseEvent(met, meh)

  override def modalTask[A](m: Modal[ModalResultWrapper[A]]) = {
    val $ = PromiseZ[A]()
    m.onClosed($ fulfill _.value)
    screen.openModal(m)
    // FIXME nasty hack to deal with the fact that in Zircon layers may hide the modal :\ So we find out all
    //       the intersections of the FOW layer and the modal and temporarily remove those elements from the
    //       layer. If it's stupid but it works, it's not stupid. It doesn't always work though :|
    val layer = map.fogOfWarLayer
    if (screen.containsLayer(layer)) {
      val removedTiles: mutable.Buffer[(Position, Tile)] = ArrayBuffer()
      m.intersectingPoints(layer).map(_.withInverseRelative(layer.getPosition)).foreach {p =>
        removedTiles += (p -> layer.getTileAt(p).get)
        layer.setTileAt(p, ZirconScreenImpl.TransparentTile)
      }
      m.onClosed(_ => removedTiles.foreach(Function.tupled(layer.setTileAt)))
    }
    $.toTask
  }
}

object ZirconScreenImpl {
  private val TransparentTile = Tiles.newBuilder().withBackgroundColor(TileColors.transparent()).build()
}

package com.nessie.view.sfx

import java.io.IOException
import javafx.event.EventHandler
import javafx.stage.WindowEvent

import com.nessie.gm.{GameState, View}
import com.nessie.model.units.CombatUnit
import common.rich.RichT._

import scala.concurrent.{Future, Promise}
import scalafx.Includes._
import scalafx.application.Platform
import scalafx.scene.Scene
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.BorderPane
import scalafx.stage.Stage

private class ScalaFxView extends View {
  private var stage: Stage = _
  private var mapGrid: MapGrid = _
  private var hasClosed = false
  Platform.runLater{
    stage = new Stage {
      scene = new Scene(800, 800)
      onCloseRequest = new EventHandler[WindowEvent] {
        override def handle(event: WindowEvent) = {
          hasClosed = true
          latestPromise.opt.foreach(_.failure(new IOException("User closed the GUI")))
          Platform.runLater(Platform.exit())
        }
      }
    }
  }
  override def updateState(gs: GameState): Unit = {
    mapGrid = new MapGrid(gs.map)
    val properties = new PropertiesPane(gs)
    mapGrid.mouseEvents.filter(_._1.eventType == MouseEvent.MouseEntered)
        .map(_._2)
        .subscribe(properties.display(_))

    val eqBar = new EventQueueBar(gs)

    Platform.runLater{
      stage.scene.get.content = new BorderPane {
        top = eqBar.node
        center = mapGrid.node
        bottom = properties.node
      }
      if (!stage.isShowing)
        stage.show()
    }
  }

  private var latestPromise: Promise[GameState] = _

  override def nextState(u: CombatUnit)(gs: GameState): Future[GameState] = {
    if (hasClosed)
      throw new IllegalStateException("The gui has been closed")
    latestPromise = mapGrid.nextState(u)(gs)
    latestPromise.future
  }
}

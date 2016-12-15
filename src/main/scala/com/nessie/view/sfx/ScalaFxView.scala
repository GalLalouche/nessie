package com.nessie.view.sfx

import com.nessie.gm.{GameState, View}
import com.nessie.model.units.CombatUnit

import scala.concurrent.Future
import scalafx.Includes._
import scalafx.application.Platform
import scalafx.scene.Scene
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.BorderPane
import scalafx.stage.Stage

private class ScalaFxView extends View {
  var stage: Stage = _
  var mapGrid: MapGrid = _
  Platform.runLater {
    stage = new Stage {
      scene = new Scene(800, 800)
    }
  }
  override def updateState(gs: GameState): Unit = {
    mapGrid = new MapGrid(gs)
    val properties = new PropertiesPane(gs)
    mapGrid.mouseEvents.filter(_._1.eventType == MouseEvent.MouseEntered)
        .map(_._2)
        .subscribe(properties.display(_))

    val eqBar = new EventQueueBar(gs)

    Platform.runLater {
      stage.scene.get.content = new BorderPane {
        top = eqBar.node
        center = mapGrid.node
        bottom = properties.node
      }
      if (!stage.isShowing)
        stage.show()
    }
  }
  override def nextState(u: CombatUnit)(gs: GameState): Future[GameState] = mapGrid.nextState(u)(gs)
}

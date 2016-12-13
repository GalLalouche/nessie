package com.nessie.map.view.sfx

import javafx.scene.{control => jfxsc, layout => jfxsl}
import javafx.{scene => jfxs}

import com.nessie.gm.{GameState, View}
import com.nessie.units.CombatUnit

import scala.concurrent.Future
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

    }
  }
  override def updateState(gs: GameState): Unit = {
    mapGrid = new MapGrid(gs)
    val properties = new PropertiesPane(gs)
    mapGrid.mouseEvents.filter(_._1.eventType == MouseEvent.MouseEntered)
        .map(_._2)
        .subscribe(properties.display(_))

    val eqBar = new EventQueueBar(gs)

    val scene = new Scene(height = 400, width = 400) {
      content = new BorderPane {
        top = eqBar.node
        center = mapGrid.node
        bottom = properties.node
      }
    }
    Platform.runLater {
      assert(stage != null)
      stage.scene = scene
      if (!stage.isShowing)
        stage.show()
    }
  }
  override def nextState(u: CombatUnit)(gs: GameState): Future[GameState] = mapGrid.nextState(u)(gs)
}

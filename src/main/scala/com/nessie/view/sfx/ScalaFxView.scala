package com.nessie.view.sfx

import com.nessie.gm.{GameState, GameStateChange, PlayerInput, View}
import com.nessie.model.map.CombatUnitObject
import com.nessie.model.units.CombatUnit
import common.rich.RichT._
import common.rich.func.{MoreObservableInstances, ToMoreMonadPlusOps}
import javafx.stage.WindowEvent
import scalafx.Includes._
import scalafx.application.Platform
import scalafx.scene.Scene
import scalafx.scene.layout.BorderPane
import scalafx.stage.Stage

import scalaz.concurrent.Task

private class ScalaFxView(customizer: ScalaFxViewCustomizer) extends View
    with MoreObservableInstances with ToMoreMonadPlusOps {
  private var stage: Stage = _
  private var mapGrid: MapGrid = _
  private var eqBar: EventQueueBar = _
  private var propPane: PropertiesPane = _
  private val logger: Logger = new Logger()
  private var hasClosed = false
  Platform.runLater {
    stage = new Stage {
      scene = new Scene(1000, 1000)
      onCloseRequest = (_: WindowEvent) => {
        hasClosed = true
        mapGrid.opt.foreach(_.killLastTask())
        Platform.runLater(Platform.exit())
      }
    }
  }

  override def updateState(gsc: GameStateChange, gs: GameState): Unit = {
    mapGrid = new MapGrid(gs.map, customizer.mapCustomizer)
    propPane = new PropertiesPane(gs)
    eqBar = new EventQueueBar(gs)
    val obs = Focuser.observer(Focuser.composite(
      mapGrid.highlighter,
      propPane.highlighter,
      eqBar.highlighter,
    ))
    mapGrid.mouseEvents
        .oMap(e => gs.map(e._2).safeCast[CombatUnitObject].map(_.unit).map(e._1 -> _))
        .subscribe(obs)
    eqBar.mouseEvents.subscribe(obs)
    Platform.runLater {
      logger.append(gsc)
      stage.scene.get.content = new BorderPane {
        top = eqBar.node
        center = mapGrid.node
        bottom = propPane.node
        right = logger.node
      }
      if (!stage.isShowing)
        stage.show()
    }
  }

  val playerInput = new PlayerInput {
    override def nextState(u: CombatUnit)(gs: GameState): Task[GameStateChange] = {
      if (hasClosed)
        throw new IllegalStateException("The gui has been closed")
      mapGrid.nextState(u)(gs).toTask
    }
  }
}

package com.nessie.view.sfx

import java.io.IOException

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

import scala.concurrent.{Future, Promise}

private class ScalaFxView extends View
    with MoreObservableInstances with ToMoreMonadPlusOps {
  private var stage: Stage = _
  private var mapGrid: MapGrid = _
  private var eqBar: EventQueueBar = _
  private var propPane: PropertiesPane = _
  private val logger: Logger = new Logger()
  private var hasClosed = false
  Platform.runLater {
    stage = new Stage {
      scene = new Scene(800, 800)
      onCloseRequest = (_: WindowEvent) => {
        hasClosed = true
        latestPromise.opt.foreach(_.failure(new IOException("User closed the GUI")))
        Platform.runLater(Platform.exit())
      }
    }
  }

  override def updateState(gsc: GameStateChange, gs: GameState): Unit = {
    mapGrid = new MapGrid(gs.map)
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

  private var latestPromise: Promise[GameStateChange] = _

  val playerInput = new PlayerInput {
    override def nextState(u: CombatUnit)(gs: GameState): Future[GameStateChange] = {
      if (hasClosed)
        throw new IllegalStateException("The gui has been closed")
      latestPromise = mapGrid.nextState(u)(gs)
      latestPromise.future
    }
  }
}

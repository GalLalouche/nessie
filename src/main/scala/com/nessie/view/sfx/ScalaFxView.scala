package com.nessie.view.sfx

import com.nessie.gm.{GameState, GameStateChange, PlayerInput, TurnAction, View}
import com.nessie.gm.GameStateChange.NoOp
import com.nessie.model.map.CombatUnitObject
import com.nessie.model.units.CombatUnit
import common.rich.RichT._
import common.rich.func.{MoreObservableInstances, ToMoreMonadPlusOps}
import javafx.stage.WindowEvent
import scalafx.Includes._
import scalafx.application.Platform
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.layout.BorderPane
import scalafx.stage.Stage

import scalaz.concurrent.Task

private class ScalaFxView(customizer: ScalaFxViewCustomizer, i: Option[Iterator[GameState]] = None) extends View
    with MoreObservableInstances with ToMoreMonadPlusOps {
  def this(customizer: ScalaFxViewCustomizer, i: Iterator[GameState]) = this(customizer, Some(i))
  private var stage: Stage = _
  private var mapGrid: MapGrid = _
  private var eqBar: EventQueueBar = _
  private var propPane: PropertiesPane = _
  private val logger: Logger = new Logger()
  private var hasClosed = false
  Platform.runLater {
    stage = new Stage {
      scene = new Scene(1200, 1200)
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

    val combatUnitObserver = Focuser.observer(Focuser.composite(
      mapGrid.highlighter,
      eqBar.highlighter,
      propPane.combatUnitHighlighter,
    ))
    mapGrid.mouseEvents
        .oMap(e => gs.map(e._2).safeCast[CombatUnitObject].map(_.unit).map(e._1 -> _))
        .subscribe(combatUnitObserver)
    eqBar.mouseEvents.subscribe(combatUnitObserver)
    mapGrid.mouseEvents.subscribe(Focuser.observer(propPane.pointHighlighter))

    Platform.runLater {
      logger.append(gsc)
      stage.scene.get.content = new BorderPane {
        top = eqBar.node
        center = mapGrid.node
        bottom = propPane.node
        right = logger.node
        i.foreach(i => {
          left = new Button("Next") {
            onAction = _ => {
              updateState(NoOp, i.next())
            }
          }
        })
      }
      if (!stage.isShowing)
        stage.show()
    }
  }

  val playerInput = new PlayerInput {
    override def nextState(u: CombatUnit)(gs: GameState): Task[TurnAction] = {
      if (hasClosed)
        throw new IllegalStateException("The gui has been closed")
      mapGrid.nextState(u)(gs).toTask
    }
  }
}

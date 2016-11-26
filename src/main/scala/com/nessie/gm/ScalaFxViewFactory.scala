package com.nessie.gm

import javafx.embed.swing.JFXPanel

import com.nessie.map.model.BattleMap
import com.nessie.map.{BattleMapObject, CombatUnitObject, EmptyMapObject}
import com.nessie.units.{CombatUnit, Skeleton, Warrior}

import scalafx.application.Platform
import scalafx.scene.{Node, Scene}
import scalafx.scene.control.Button
import scalafx.scene.layout.GridPane
import scalafx.stage.Stage

object ScalaFxViewFactory extends ViewFactory {
  new JFXPanel
  Platform.implicitExit = false
  private def text(o: BattleMapObject): String = o match {
    case EmptyMapObject => ""
    case CombatUnitObject(u) => u match {
      case w: Warrior => "W"
      case s: Skeleton => "S"
    }
  }
  private def cell(o: BattleMapObject): Node = {
    new Button(text(o)) {
      prefHeight = 40
      prefWidth = 40
    }
  }
  private class ScalaFxView() extends View {
    override def updateState(gs: GameState): Unit = {
      println(Platform.implicitExit)
      val s = new Scene(height = 400, width = 400) {
        content = new GridPane() {
          val labels = for ((p, o) <- gs.map.points) yield {
            val $ = cell(o)
            GridPane.setConstraints($, p.x, p.y)
            $
          }
          children = labels
        }
      }
      Platform.runLater {
        val stage = new Stage {
          scene = s
        }
        stage.showAndWait()
      }
      println(Platform.implicitExit)
    }
    override def requirePlayerInput(combatUnit: CombatUnit)(map: BattleMap): BattleMap = ???
  }
  override def create(): View = new ScalaFxView()
}

package com.nessie.map.view.sfx

import javafx.scene.{control => jfxsc, layout => jfxsl}
import javafx.{scene => jfxs}

import com.nessie.gm.GameState
import com.nessie.map.model.MapPoint
import com.nessie.map.{BattleMapObject, CombatUnitObject, EmptyMapObject}
import com.nessie.units.{CombatUnit, Skeleton, Warrior}
import common.rich.RichT._
import rx.lang.scala.subjects.PublishSubject

import scala.concurrent.{Future, Promise}
import scalafx.Includes._
import scalafx.geometry.Side
import scalafx.scene.Node
import scalafx.scene.control.Button
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.GridPane

private class MapGrid(gs: GameState) extends NodeWrapper {
  val mouseEvents = PublishSubject[(MouseEvent, MapPoint)]()
  private def text(o: BattleMapObject): String = o match {
    case EmptyMapObject => ""
    case CombatUnitObject(u) => u match {
      case w: Warrior => "W"
      case s: Skeleton => "S"
    }
  }
  private def cell(o: BattleMapObject, p: MapPoint): Node = {
    val $ = new Button(text(o)) {
      prefHeight = 40
      prefWidth = 40
    }
    toObserver(MouseEvent.Any, $)
        .map(new MouseEvent(_)) // piece of shit framework :\
        // TODO fproduct
        .map(e => e -> p) subscribe mouseEvents
    $
  }
  val node = new GridPane() {
    val labels = for ((p, o) <- gs.map.points) yield {
      val $ = cell(o, p)
      GridPane.setConstraints($, p.x, p.y)
      $
    }
    children = labels
  }

  private def highlight(location: MapPoint): Unit = {
    val cell = node.children
        .find(n => jfxsl.GridPane.getColumnIndex(n) == location.x && jfxsl.GridPane.getRowIndex(n) == location.y)
        .get
        .mapTo(jfx2sfx)
    cell.style = """-fx-base: blue"""
  }

  private def getMove(source: MapPoint, gs: GameState): Future[GameState] = {
    val p = Promise[GameState]
    val menuEvents = PublishSubject[GameState]()
    val menuFactory = new ActionMenuFactory(source, gs, menuEvents)
    def createMenu(node: Node, destination: MapPoint): Unit = {
      menuFactory(destination).show(node, Side.Bottom, 0, 0)
    }
    val subscription = mouseEvents.filter(_._1.eventType == MouseEvent.MouseClicked)
        .map(e => jfx2sfx(e._1.source.asInstanceOf[jfxs.Node]) -> e._2) subscribe (e => createMenu(e._1, e._2))
    menuEvents.subscribe(e => {
      subscription.unsubscribe
      p success e
    })
    p.future
  }

  def nextState(u: CombatUnit)(gs: GameState): Future[GameState] = {
    val location: MapPoint = CombatUnitObject.findIn(u, gs.map).get
    highlight(location)
    getMove(location, gs)
  }
}

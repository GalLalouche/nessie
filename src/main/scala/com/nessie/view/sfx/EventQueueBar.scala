package com.nessie.view.sfx

import com.nessie.gm.{GameState, UnitTurn}
import com.nessie.model.units.CombatUnit
import common.rich.func.MoreIterableInstances
import rx.lang.scala.Observable
import scalafx.scene.control.Label
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.HBox

import scalaz.syntax.ToFunctorOps

private class EventQueueBar(gameState: GameState) extends NodeWrapper with Highlighter[CombatUnit]
    with ToFunctorOps with MoreIterableInstances {
  private val labels = gameState.eq.take(10).map(_.asInstanceOf[UnitTurn].u).fproduct(e => Label(NodeWrapper.shortName(e)))
  val mouseEvents: Observable[(MouseEvent, CombatUnit)] = NodeWrapper mouseEvents labels
  override def highlight(u: CombatUnit): Unit = labels.filter(_._1 == u).map(_._2).foreach(NodeWrapper.setBackgroundColor("teal"))
  override def disableHighlighting(u: CombatUnit) = labels.map(_._2).foreach(NodeWrapper.setBackgroundColor("white"))
  val node = new HBox(10) {
    children = labels.map(_._2)
  }
}

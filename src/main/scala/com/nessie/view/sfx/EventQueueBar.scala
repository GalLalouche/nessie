package com.nessie.view.sfx

import com.nessie.gm.{GameState, UnitTurn}
import com.nessie.model.units.CombatUnit
import com.nessie.view.sfx.RichNode._
import common.rich.func.MoreIterableInstances
import rx.lang.scala.Observable
import scalafx.scene.control.Label
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.HBox

import scalaz.syntax.ToFunctorOps

private class EventQueueBar(gameState: GameState)
    extends ToFunctorOps with MoreIterableInstances {
  private val labels = gameState.eq.take(10).map(_.asInstanceOf[UnitTurn].u).fproduct(e => Label(NodeUtils.shortName(e)))
  val mouseEvents: Observable[(MouseEvent, CombatUnit)] = NodeUtils mouseEvents labels
  val highlighter = new Focuser[CombatUnit] {
    override def focus(u: CombatUnit): Unit =
      labels.filter(_._1 == u).map(_._2).foreach(_.setBackgroundColor("teal"))
    override def unfocus(u: CombatUnit) =
      labels.map(_._2).foreach(_.setBackgroundColor("white"))
  }
  val node = new HBox(10) {
    children = labels.map(_._2)
  }
}

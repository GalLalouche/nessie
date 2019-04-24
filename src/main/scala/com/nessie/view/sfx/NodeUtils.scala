package com.nessie.view.sfx

import com.nessie.model.units.CombatUnit
import com.nessie.view.sfx.RichNode._
import common.rich.func.MoreObservableInstances
import rx.lang.scala.Observable
import scalafx.scene.input.MouseEvent

import scalaz.syntax.ToFunctorOps

private object NodeUtils
    extends ToFunctorOps with MoreObservableInstances {
  def mouseEvents[Key, N: NodeLike](m: Traversable[(Key, N)]): Observable[(MouseEvent, Key)] = m.map {
    case (key, node) => implicitly[NodeLike[N]].scalaNode(node).mouseEvents.strengthR(key)
  }.foldLeft[Observable[(MouseEvent, Key)]](Observable.empty)(_ merge _)
  def shortName(u: CombatUnit): String = u.metadata.name.take(3)
}

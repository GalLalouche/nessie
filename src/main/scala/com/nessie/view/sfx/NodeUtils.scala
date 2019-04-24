package com.nessie.view.sfx

import com.nessie.model.units.CombatUnit
import com.nessie.view.sfx.RichNode._
import common.rich.func.MoreObservableInstances
import rx.lang.scala.Observable
import rx.lang.scala.subjects.PublishSubject
import scalafx.scene.input.MouseEvent

import scalaz.syntax.ToFunctorOps

private object NodeUtils
    extends ToFunctorOps with MoreObservableInstances {
  def mouseEvents[Key, N: NodeLike](ns: Traversable[(Key, N)]): Observable[(MouseEvent, Key)] = {
    val $ = PublishSubject[(MouseEvent, Key)]()
    // TODO move to RichObservable
    for ((key, node) <- ns) implicitly[NodeLike[N]].scalaNode(node).mouseEvents.strengthR(key).subscribe($)
    $
  }
  def shortName(u: CombatUnit): String = u.metadata.name.take(3)
}

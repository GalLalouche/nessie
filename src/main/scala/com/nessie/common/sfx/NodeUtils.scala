package com.nessie.common.sfx

import com.nessie.common.sfx.RichNode._
import com.nessie.model.units.CombatUnit
import rx.lang.scala.Observable
import scalafx.scene.input.MouseEvent

import scalaz.syntax.ToFunctorOps
import common.rich.func.{MoreObservableInstances, TuplePLenses}

import common.rich.RichObservable
import common.rich.RichTuple._

object NodeUtils
    extends ToFunctorOps with MoreObservableInstances {
  def mouseEvents[Key, N: NodeLike](ns: Traversable[(Key, N)]): Observable[(MouseEvent, Key)] = {
    val keyedObservables: Iterator[(Key, Observable[MouseEvent])] = ns.toIterator
        .map(TuplePLenses.tuple2Second.modify(implicitly[NodeLike[N]].scalaNode(_).mouseEvents))
    RichObservable.concat(keyedObservables.map(_.swap.reduce(_ strengthR _)))
  }
  def shortName(u: CombatUnit): String = u.metadata.name.take(3)
}

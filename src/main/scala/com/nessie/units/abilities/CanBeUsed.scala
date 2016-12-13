package com.nessie.units.abilities

import common.rich.func.RichMonadPlus._
import common.rich.RichT._

import com.nessie.map.model.{BattleMap, MapPoint}

import scalaz.std.ListInstances

private trait CanBeUsed {
  def apply(battleMap: BattleMap, source: MapPoint, destination: MapPoint): Boolean
}

private object CanBeUsed extends ListInstances {
  private def getCompanionObject(clazz: Class[_]): Option[Any] = {
    import scala.reflect.runtime.{currentMirror => cm}
    try {
      Some(cm.classSymbol(clazz).companion)
          .filter(_.isModule)
          .map(_.asModule)
          .map(cm.reflectModule(_).instance)
    } catch {
      case _: ClassNotFoundException => None
    }
  }
  def extract(ability: UnitAbility): CanBeUsed =
    (ability.getClass :: ability.getClass.getInterfaces.toList)
        .flatMap(getCompanionObject)
        .select[CanBeUsed]
        .mapTo(new CompositeCanBeUsed(_))
}

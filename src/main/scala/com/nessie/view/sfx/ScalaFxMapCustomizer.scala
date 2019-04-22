package com.nessie.view.sfx

import com.nessie.model.map.BattleMapObject

trait ScalaFxMapCustomizer {
  def text: PartialFunction[BattleMapObject, String] = PartialFunction.empty
  def cellColor: PartialFunction[BattleMapObject, String] = PartialFunction.empty

}

object ScalaFxMapCustomizer {
  def none: ScalaFxMapCustomizer = new ScalaFxMapCustomizer {}
}

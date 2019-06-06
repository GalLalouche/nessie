package com.nessie.view.zirconview.screen

import com.nessie.model.map.BattleMap
import enumeratum.{Enum, EnumEntry}

private[zirconview] sealed trait DebugButton extends EnumEntry

private[zirconview] object DebugButton extends Enum[DebugButton] {
  override val values = findValues
  case class FinishAll(map: BattleMap) extends DebugButton
}


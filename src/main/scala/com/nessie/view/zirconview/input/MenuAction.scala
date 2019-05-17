package com.nessie.view.zirconview.input

import com.nessie.gm.TurnAction

private sealed trait MenuAction

private object MenuAction {
  case object Cancelled extends MenuAction
  case class Action(ua: TurnAction) extends MenuAction
}

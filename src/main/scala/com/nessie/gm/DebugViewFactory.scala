package com.nessie.gm

/** A view factory that exposers a special view with debugging UI. */
trait DebugViewFactory extends ViewFactory {
  def create(s: DebugMapStepper): View
}

package com.nessie.view.zirconview

import com.google.inject.Inject
import com.nessie.gm.{DebugMapStepper, DebugViewFactory, View}
import common.rich.RichT._

class ZirconViewFactory extends DebugViewFactory {
  @Inject(optional = true) private var zirconViewCustomizer = ZirconViewCustomizer.Null
  override def create(stepper: DebugMapStepper): View = {
    new ZirconView(zirconViewCustomizer, stepper) <| (_.nextSmallStep())
  }
  override def create(): View =
    new ZirconView(zirconViewCustomizer, DebugMapStepper.Null)
}

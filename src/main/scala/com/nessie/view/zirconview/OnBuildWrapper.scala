package com.nessie.view.zirconview

import org.hexworks.zircon.api.component.{Component, ComponentBuilder}

private class OnBuildWrapper[C <: Component, B <: ComponentBuilder[C, B]](
    val cb: ComponentBuilder[C, B], onBuild: C => Any) {
  def build(): C = {
    val $ = cb.build
    onBuild($)
    $
  }
}

private object OnBuildWrapper {
  def apply[C <: Component, B <: ComponentBuilder[C, B]](cb: ComponentBuilder[C, B])(onBuild: C => Any) =
    new OnBuildWrapper(cb, onBuild)
  def noOp[C <: Component, B <: ComponentBuilder[C, B]](cb: ComponentBuilder[C, B]): OnBuildWrapper[C, B] =
    apply(cb)(_ => ())
}

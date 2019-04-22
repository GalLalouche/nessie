package com.nessie.view.sfx

trait ScalaFxViewCustomizer {
  def mapCustomizer: ScalaFxMapCustomizer = ScalaFxMapCustomizer.none
}

object ScalaFxViewCustomizer {
  def none: ScalaFxViewCustomizer = new ScalaFxViewCustomizer {}
}

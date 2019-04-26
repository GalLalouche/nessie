package com.nessie.view.zirconview

trait ZirconViewCustomizer {
  def mapCustomizer: ZirconMapCustomizer
}

object ZirconViewCustomizer {
  val Null: ZirconViewCustomizer = new ZirconViewCustomizer {
    override val toString = "NullCustomizer"
    override def mapCustomizer = ZirconMapCustomizer.Null
  }
}

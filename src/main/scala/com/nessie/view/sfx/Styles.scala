package com.nessie.view.sfx

private object Styles {
  def style(property: String, value: String): String = s"$property: $value"
  val FontWeight = "-fx-font-weight"
  def fontWeight(value: String): String = style(FontWeight, value)

  val BackgroundColor = "-fx-background-color"
  def backgroundColor(value: String): String = style(BackgroundColor, value)

  val BaseColor = "-fx-base"
  def baseColor(value: String): String = style(BaseColor, value)
}

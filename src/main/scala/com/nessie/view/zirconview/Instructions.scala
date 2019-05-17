package com.nessie.view.zirconview

private sealed trait Instructions

private object Instructions {
  case object BasicInput extends Instructions
}

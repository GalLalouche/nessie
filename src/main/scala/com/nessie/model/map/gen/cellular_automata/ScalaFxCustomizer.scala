package com.nessie.model.map.gen.cellular_automata

import com.nessie.view.sfx.{ScalaFxMapCustomizer, ScalaFxViewCustomizer}

private object ScalaFxCustomizer extends ScalaFxViewCustomizer {
  override def mapCustomizer = new ScalaFxMapCustomizer {
    override def text = {
      case Wall(_) => ""
      case Empty(_) => ""
    }
    override def cellColor = {
      case Wall(_) => "Orange"
      case Empty(_) => "cyan"
    }
  }
}

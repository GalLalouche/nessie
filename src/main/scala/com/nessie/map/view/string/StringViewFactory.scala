package com.nessie.map.view.string

import com.nessie.gm.{View, ViewFactory}

object StringViewFactory extends ViewFactory {
  override def create(): View = new StringBattleMapViewer
}

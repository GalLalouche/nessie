package com.nessie.view

import com.google.inject.Provides
import com.nessie.gm.{DebugViewFactory, ViewFactory}
import com.nessie.view.zirconview.ZirconViewFactory
import net.codingwell.scalaguice.ScalaModule

object ViewModule extends ScalaModule {
  override def configure(): Unit = {
    bind[DebugViewFactory].to[ZirconViewFactory]
  }

  @Provides
  private def provideViewFromDebugView(dvf: DebugViewFactory): ViewFactory = dvf
}

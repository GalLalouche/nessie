package com.nessie.view.zirconview

import common.rich.RichT._
import common.rich.collections.RichIterator._
import org.hexworks.zircon.api.{Components, Positions, Sizes}
import org.hexworks.zircon.api.component.{Component, Panel}

import scala.collection.JavaConverters._

private object DebugButtonBuilder {
  type ButtonProperties = (String, () => Any)
  def apply(p: Placer, bps: OnBuildWrapper[_ <: Component, _]*): Panel = {
    val $ = Components.panel
        .withTitle("Debug")
        .withSize(Sizes.create(20, 80))
        .wrapWithBox(true)
        .<|(p)
        .build
    bps.foreach {ob =>
      val children = $.getChildren.iterator.asScala
      val position =
      // TODO RichIterator.lastOption
        if (children.isEmpty) Positions.zero else Positions.create(-1, 0).relativeToBottomOf(children.last)
      ob.cb.withPosition(position)
      $.addComponent(ob.build())
    }
    $
  }
}

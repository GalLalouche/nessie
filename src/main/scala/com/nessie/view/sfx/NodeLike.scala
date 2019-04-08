package com.nessie.view.sfx

import javafx.scene.{control => jfxsc, layout => jfxl, Node => JNode}
import scalafx.scene.{control => sfxsc, layout => sfxl, Node => SNode}

private trait NodeLike[N] {
  def javaNode(n: N): JNode
  def scalaNode(n: N): SNode
}

private object NodeLike {
  implicit def scalaNodeLike[N <: SNode]: NodeLike[N] = new NodeLike[N] {
    override def javaNode(n: N): JNode = n.delegate
    override def scalaNode(n: N): SNode = n
  }
  implicit def javaNodeLike[N <: JNode]: NodeLike[N] = new NodeLike[N] {
    override def javaNode(n: N): JNode = n
    override def scalaNode(n: N): SNode = n match {
      case b: jfxsc.Button => new sfxsc.Button(b)
      case l: jfxsc.Label => new sfxsc.Label(l)
      case b: jfxl.BorderPane => new sfxl.BorderPane(b)
      case _ => throw new UnsupportedOperationException("Unsupported conversion for " + n.getClass)
    }
  }
}

package com.nessie.view.sfx

import javafx.scene.{Node => JNode}
import scalafx.application.Platform.runLater
import scalafx.scene.Node

private object RichNode {
  implicit class richNode(n: Node) {
    def setBackgroundColor(color: String): Unit = runLater(n.setStyle(Styles.backgroundColor(color)))
    def setBaseColor(color: String): Unit = runLater(n.setStyle(Styles.baseColor(color)))
    def setFontWeight(style: String): Unit = runLater(n.setStyle(Styles.fontWeight(style)))
  }

  implicit class richJNode(n: JNode) extends richNode(NodeWrapper.jfx2sfx(n))
}

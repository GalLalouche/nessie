package com.nessie.model.map.gen

import java.awt.image.BufferedImage
import java.awt.Color

import com.nessie.common.sfx.RichNode._
import com.nessie.model.map.gen.DemoImageViewer.LazySeq
import com.nessie.model.map.{GridSize, MapPoint}
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.embed.swing.SwingFXUtils
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.image.ImageView
import scalafx.scene.input.KeyCode
import scalafx.scene.layout.{BorderPane, GridPane}

import common.rich.collections.RichIterable._

private abstract class DemoImageViewer extends JFXApp {
  protected def mapFunction: PartialFunction[Int, Stream[BufferedImage]]
  private lazy val maps: Seq[Seq[BufferedImage]] = new LazySeq(mapFunction)
  private var currentX = 0
  private var currentWidth: Int = 1
  private var currentY = 0
  private var currentHeight: Int = 1
  val mapTracker = new GridPane {
    prefHeight = 300
    prefWidth = 1300
  }
  val bp = new BorderPane {
    padding = Insets(25)
    right = mapTracker
  }
  stage = new PrimaryStage {
    scene = new Scene {
      root = bp
      redraw()
      private def redraw(): Unit = {
        bp.center = new ImageView(SwingFXUtils.toFXImage(maps(currentY)(currentX), null))
        mapTracker.children.clear()
        for {
          i <- 0 until currentWidth
          j <- 0 until currentHeight
        } {
          val node = new BorderPane {
            prefHeight = 10
            prefWidth = 10
          }
          if (i == currentX && j == currentY)
            node.setBackgroundColor("Green")
          else if (i == currentX || j == currentY)
            node.setBackgroundColor("Red")
          else
            node.setBackgroundColor("Black")
          GridPane.setConstraints(node, i, j)
          mapTracker.children.add(node)
        }
      }
      this.keyEvents.subscribe {event =>
        event.code match {
          case KeyCode.J =>
            if (maps.checkLength(currentY + 2) != Smaller) {
              currentY += 1
              currentHeight = math.max(currentHeight, currentY + 1)
              currentX = 0
              redraw()
            }
          case KeyCode.K =>
            if (currentY != 0) {
              currentY -= 1
              currentX = 0
              redraw()
            }
          case KeyCode.L =>
            if (maps(currentY).checkLength(currentX + 2) != Smaller) {
              currentX += 1
              currentWidth = math.max(currentWidth, currentX + 1)
              redraw()
            }
          case KeyCode.Digit0 =>
            currentX = 0
            redraw()
          case KeyCode.H =>
            if (currentX != 0) {
              currentX -= 1
              redraw()
            }
          case KeyCode.Digit4 if event.shiftDown =>
            while(currentX < maps(currentY).length - 1) {
              currentX += 1
              redraw()
            }
          case _ =>
            ()
        }
      }
    }
  }
}

private object DemoImageViewer {
  private class LazySeq[A](f: PartialFunction[Int, A]) extends Seq[A] {
    private val stream = Stream.iterate(0)(_ + 1).map(f.lift).takeWhile(_.isDefined).map(_.get)
    override def length = stream.length
    override def apply(idx: Int) = stream(idx)
    override def iterator = stream.iterator
  }

  private[gen] val ImageScale = 20
  private[gen] def translate(mp: MapPoint) = MapPoint(mp.x * ImageScale, mp.y * ImageScale)
  private[gen] def translate(gs: GridSize) = GridSize(gs.width * ImageScale, gs.height * ImageScale)
  private[gen] val RoomColor = Color.GRAY
  private[gen] val GridColor = Color.BLACK
}

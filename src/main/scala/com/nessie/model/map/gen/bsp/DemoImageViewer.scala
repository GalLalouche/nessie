package com.nessie.model.map.gen.bsp

import java.awt.image.BufferedImage

import com.nessie.common.rng.StdGen
import com.nessie.common.sfx.RichNode._
import com.nessie.model.map.GridSize
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.embed.swing.SwingFXUtils
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.image.ImageView
import scalafx.scene.input.KeyCode
import scalafx.scene.layout.{BorderPane, GridPane}

import common.rich.collections.RichIterable._

private object DemoImageViewer extends JFXApp {
  private class LazySeq[A](f: PartialFunction[Int, A]) extends Seq[A] {
    override def length = iterator.length
    override def apply(idx: Int) = f(idx)
    override def iterator =
      Iterator.iterate(0)(_ + 1).map(f.lift).takeWhile(_.isDefined).map(_.get)
  }
  private val generator = new Generator(GridSize(75, 75))
  private val stdGen = StdGen(2)
  private var currentX = 0
  private var currentWidth: Int = 1
  private var currentY = 0
  private var currentHeight: Int = 1
  private val maps: Seq[Stream[BufferedImage]] = {
    val base = generator.partitions.mkRandom(stdGen.split._1).toStream
    lazy val rooms = Rooms(base.last).map(_.toStream).mkRandom(stdGen.split._2)
    new LazySeq({
      case 0 => base.map(_.toImage)
      case 1 => rooms.map(_.toImage)
      case 2 => Stream(JoinedRooms(rooms.last).toImage)
    })
  }
  val mapTracker = new GridPane {
    prefHeight = 300
    prefWidth = 1000
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
            prefHeight = 20
            prefWidth = 20
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
          case KeyCode.H =>
            if (currentX != 0) {
              currentX -= 1
              redraw()
            }
          case _ =>
            ()
        }
      }
    }
  }
}

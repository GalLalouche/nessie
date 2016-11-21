package com.nessie.view.swing

import java.awt

import com.nessie.view.swing.RichGrid.ItemClicked

import scala.collection.mutable
import scala.swing.event.{Event, MouseClicked}
import scala.swing._

/** A smarter rich grid, that knows it's a fucking grid. */
class RichGrid(rows: Int, cols: Int, map: Map[(Int, Int), Component]) extends GridPanel(rows, cols) {
	val iterator = map.iterator
	// swing's grid panel is filled by columns first
	for (y <- 0 until rows; x <- 0 until cols) {
		val c = map(x -> y)
		c.listenTo(c.mouse.clicks)
		c.reactions += { case e: MouseClicked => publish(ItemClicked(c, x, y)) }
		contents += c
	}

	def apply(x: Int, y: Int): Component = map((x, y))
}

object RichGrid {
	case class ItemClicked(c: Component, x: Int, y: Int) extends Event
	def apply(rows: Int, cols: Int, content: Seq[Component]): RichGrid = {
		def toGrid[T](xs: Seq[T]): Map[(Int, Int), T] = {
			val iterator = xs.iterator
			val map = new mutable.HashMap[(Int, Int), T] // SHAME
			for (x <- 0 until cols;
			     y <- 0 until rows) {
				map += (x, y) -> iterator.next
			}
			map.toMap
		}
		new RichGrid(rows, cols, toGrid(content))
	}
}

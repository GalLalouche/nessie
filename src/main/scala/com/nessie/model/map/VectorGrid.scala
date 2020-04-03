package com.nessie.model.map

import scala.collection.mutable.ArrayBuffer

import common.rich.RichT._

/**
 * Trades space for (amortized) performance. Unlike DictMap which doesn't need to save empty objects, this
 * class does. In addition, it has to allocate the entire map on construction. However, since it doesn't need
 * to do any hashing of map points it has much better indexing performance.
 */
object VectorGrid extends GridFactory {
  override def apply[A](gs: GridSize, initialObject: A): Grid[A] = Impl(
    gridRows = Vector.fill(gs.height, gs.width)(initialObject),
    width = gs.width,
    height = gs.height,
  )
  override def apply[A](rows: Seq[Seq[A]]): Grid[A] = {
    val vr = GridFactoryHelper.verify(rows)
    Impl(vr.rows.view.map(_.toVector).toVector, width = vr.gs.width, height = vr.gs.height)
  }

  private case class Impl[A](
      gridRows: Vector[Vector[A]],
      override val width: Int,
      override val height: Int,
  ) extends Grid[A] {
    assert(gridRows.size == height)
    assert(gridRows.head.size == width)
    @inline override final def apply(p: MapPoint): A = gridRows(p.y)(p.x)
    // TODO lenses?
    @inline override final def place(p: MapPoint, o: A): Impl[A] =
      copy(gridRows = gridRows.updated(p.y, gridRows(p.y).updated(p.x, o)))
    override def map[B](f: A => B): Grid[B] = copy(gridRows = gridRows.map(_.map(f)))
    override def mapPoints[B](f: (MapPoint, A) => B): Grid[B] = {
      val $ = ArrayBuffer.fill(height)(ArrayBuffer[B]() <| (_.sizeHint(width)))
      for (y <- 0 until height; x <- 0 until width) $(y) += f(MapPoint(x, y), gridRows(y)(x))
      copy($.view.map(_.toVector).toVector)
    }
  }
}

package com.nessie.model.map

/**
 * Trades space for (amortized) performance. Unlike DictMap which doesn't need to save empty objects, this
 * class does. In addition, it has to allocate the entire map on construction. However, since it doesn't need
 * to do any hashing of map points it has much better indexing performance.
 */
case class VectorGrid[A] private(
    gridRows: Vector[Vector[A]],
    override val width: Int,
    override val height: Int,
) extends Grid[A] {
  @inline override final def apply(p: MapPoint): A = gridRows(p.y)(p.x)
  // TODO lenses?
  @inline override final def place(p: MapPoint, o: A): VectorGrid[A] =
    copy(gridRows = gridRows.updated(p.y, gridRows(p.y).updated(p.x, o)))
  override def map[B](f: A => B): Grid[B] = copy(gridRows = gridRows.map(_.map(f)))
}

object VectorGrid extends GridFactory {
  override def apply[A](gs: GridSize, initialObject: A): VectorGrid[A] = new VectorGrid(
    gridRows = Vector.fill(gs.height, gs.width)(initialObject),
    width = gs.width,
    height = gs.height,
  )
  override def apply[A](rows: Seq[Seq[A]]): Grid[A] = {
    val vr = GridFactoryHelper.verify(rows)
    VectorGrid(vr.rows.view.map(_.toVector).toVector, width = vr.gs.width, height = vr.gs.height)
  }
}

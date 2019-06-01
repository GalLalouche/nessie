package com.nessie.model.map

/**
 * Trades space for (amortized) performance. Unlike DictMap which doesn't need to save empty objects, this
 * class does. In addition, it has to allocate the entire map on construction. However, since it doesn't need
 * to do any hashing of map points it has much better indexing performance.
 */
case class VectorGrid[A] private(
    grid: Vector[Vector[A]],
    override val width: Int,
    override val height: Int,
) extends Grid[A] {
  @inline override final def apply(p: MapPoint): A = grid(p.x)(p.y)
  // TODO lenses?
  @inline override final def place(p: MapPoint, o: A): VectorGrid[A] =
    copy(grid = grid.updated(p.x, grid(p.x).updated(p.y, o)))
  override def map(f: A => A): Grid[A] = copy(grid = grid.map(_.map(f)))
}

object VectorGrid extends GridFactory {
  override def apply[A](gs: GridSize, initialObject: A): VectorGrid[A] = new VectorGrid(
    grid = Vector.fill(gs.width, gs.height)(initialObject),
    width = gs.width,
    height = gs.height,
  )
}

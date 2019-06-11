package com.nessie.model.map

import common.rich.collections.RichTraversableOnce._

private object GridFactoryHelper {
  case class VerifiedRows[A](rows: Seq[Seq[A]], gs: GridSize)
  def verify[A](rows: Seq[Seq[A]]): VerifiedRows[A] = {
    require(rows.hasSameValues(_.size))
    val width = rows.head.size
    val height = rows.size
    VerifiedRows(rows, GridSize(width = width, height = height))
  }
}

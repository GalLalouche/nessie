package com.nessie.model.map

import common.rich.collections.RichTraversableOnce._

object GridPrinter {
  trait CharPrintable[A] {
    def apply(a: A): Char
  }
  object CharPrintable {
    def apply[A](f: A => Char): CharPrintable[A] = f(_)
    implicit val IntEv: CharPrintable[Int] = apply(_.toString.iterator.single)
    implicit val CharEv: CharPrintable[Char] = apply(identity)
  }
  def apply[A: CharPrintable](g: Grid[A]): String = {
    val rows = 0.until(g.height).map(y => 0.until(g.width).map(x => g(MapPoint(x, y))))
    rows.map(_.map(implicitly[CharPrintable[A]].apply).mkString(",")).mkString("\n")
  }
}

package com.nessie.model.map

import com.nessie.model.map.GridParser.CharParsable
import common.rich.collections.RichTraversableOnce._
import common.rich.primitives.RichBoolean._

import scalaz.{-\/, \/, \/-}

trait GridParser {
  def parse[A: CharParsable](grid: String): Grid[A]
}

object GridParser {
  trait CharParsable[A] {
    def apply(c: Char): A
  }
  object CharParsable {
    def apply[A](f: Char => A): CharParsable[A] = f(_)
    implicit val IntEv: CharParsable[Int] = _.toString.toInt
  }
  class InvalidGridStringException(grid: String) extends Exception(s"Invalid Grid string:\n$grid")

  def fromFactory(gf: GridFactory): GridParser = new GridParser {
    override def parse[A](s: String)(implicit ev: CharParsable[A]) = {
      val lines: InvalidGridStringException \/ Seq[Seq[A]] = {
        require(s.nonEmpty)
        val seq = s.split("\n").toVector
        if (seq.hasSameValues(_.length).isFalse)
          -\/(new InvalidGridStringException(s))
        else
          \/-(seq.map(_.map(ev.apply)))
      }
      lines match {
        case -\/(a) => throw a
        case \/-(lines) => gf.apply(lines)
      }
    }
  }
}

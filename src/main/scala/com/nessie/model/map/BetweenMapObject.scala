package com.nessie.model.map

import common.rich.RichT._

trait BetweenMapObject {
  override def toString = this.simpleName
}

object EmptyBetweenMapObject extends BetweenMapObject
object Wall extends BetweenMapObject

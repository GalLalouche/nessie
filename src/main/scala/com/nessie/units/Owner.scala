package com.nessie.units

sealed trait Owner

object Owner {
  object AI extends Owner
  object Player extends Owner
}

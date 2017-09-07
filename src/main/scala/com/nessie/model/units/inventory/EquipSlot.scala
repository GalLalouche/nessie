package com.nessie.model.units.inventory

/**
 * An equipment slot on a character, just as the head, torso, or left/right hand.
 * Every slot has a type, but there can be several slots for each type: e.g., left/right
 * hand, two rings, etc.
 */
sealed abstract class EquipSlot(val equipType: EquipType)
import common.rich.collections.RichTraversableOnce._
import common.rich.primitives.RichBoolean._

object EquipSlot {
  object Head extends EquipSlot(EquipType.Head)
  object Torso extends EquipSlot(EquipType.Torso)
  object RightHand extends EquipSlot(EquipType.Hand)
  object LeftHand extends EquipSlot(EquipType.Hand)
  val values: Traversable[EquipSlot] = { // extract all hosts by reflection
    import scala.reflect.runtime.{universe => u}
    u.typeOf[EquipSlot.type]
        .decls
        .flatMap(e => e.isModule.ifTrue(e.asModule))
        .map(e => u.runtimeMirror(getClass.getClassLoader).reflectModule(e).instance.asInstanceOf[EquipSlot])
  }
  val default: EquipType => EquipSlot = values.toMultiMap(_.equipType).mapValues(_.head)
}

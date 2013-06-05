package com.nessie.units

import org.scalatest.matchers.{BePropertyMatchResult, BePropertyMatcher}
import scala.reflect.Manifest

class CombatUnitTests extends HasHPTests {
	protected def instanceOf[T](implicit manifest: Manifest[T]) =
		new BePropertyMatcher[Any] {
			def apply(left: Any) = {
				val clazz = manifest.runtimeClass.asInstanceOf[Class[T]]
				new BePropertyMatchResult (left.getClass == clazz, "instance of " + clazz.getSimpleName)
			}
		}

	override def $: CombatUnit = new CombatUnit (10)

	"Reduce" should "return a combat unit" in {
		$.reduceHp (5) should be an instanceOf[CombatUnit]
	}

	"Heal" should "return a combat unit" in {
		$.healHp (5) should be an instanceOf[CombatUnit]
	}
}


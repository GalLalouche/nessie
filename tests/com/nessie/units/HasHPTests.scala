package com.nessie.units

import org.scalamock.scalatest.MockFactory
import org.scalatest.{OneInstancePerTest, FlatSpec}
import org.scalatest.matchers._
import scala.reflect.Manifest

class HasHPTests extends FlatSpec with ShouldMatchers with MockFactory with OneInstancePerTest {
	protected def instanceOf[T](implicit manifest: Manifest[T]) =
		new BePropertyMatcher[Any] {
			def apply(left: Any) = {
				val clazz = manifest.runtimeClass.asInstanceOf[Class[T]]
				new BePropertyMatchResult(left.getClass == clazz, "instance of " + clazz.getSimpleName)
			}
		}

	def $: HasHP = new HasHP(10)

	"Constructor" should "return the correct maxHP" in {
		new HasHP(10).maxHp should be === 10
	}

	it should "throw IllegalArgumentException on zero hp" in {
		evaluating {
			new HasHP(0)
		} should produce[IllegalArgumentException]
	}

	it should "have the same max hp and current hp" in {
		new HasHP(10).currentHp should be === 10
	}

	"Reduce" should "throw exception on negative amount" in {
		evaluating {
			$.reduceHp(-1)
		} should produce[IllegalArgumentException]
	}

	it should "return an object with the new current HP" in {
		$.reduceHp(5).currentHp should be === 5
	}

	it should "return an object with the same max HP" in {
		$.reduceHp(5).maxHp should be === 10
	}

	it should "return an object with 0 hp if reduction is larger than current" in {
		$.reduceHp(11).currentHp should be === 0
	}

	it should "return an object of the same type as the original" in {
		$.reduceHp(5).getClass should be === $.getClass
	}

	"Heal" should "throw exception on negative amount" in {
		evaluating {
			$.healHp(-1)
		} should produce[IllegalArgumentException]
	}

	it should "return an object with the new current HP" in {
		$.reduceHp(5).healHp(3).currentHp should be === 8
	}

	it should "return an object with the same max HP" in {
		$.reduceHp(5).healHp(3).maxHp should be === 10
	}

	it should "return an object with max hp if heal + current is larger than max" in {
		$.reduceHp(5).healHp(10).currentHp should be === 10
	}

	it should "return an object of the same type as the original" in {
		$.healHp(5).getClass should be === $.getClass
	}

	"isDead" should "return true if currentHp is 0" in {
		$.reduceHp(10).isDead should be === true
	}

	it should "return false if currentHp is not 0" in {
		$.isDead should be === false
	}

	"getAttacked" should "return an object minus the attack's damage" in {
		$.getAttacked(Attack(5)).currentHp should be === 5
	}

	it should "return an object of the same type as the original" in {
		$.getAttacked(Attack(5)).getClass should be === $.getClass
	}
}


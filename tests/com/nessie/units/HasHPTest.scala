package com.nessie.units

import com.nessie.units.abilities.Attack
import org.scalatest.{FlatSpec, Matchers}

class HasHPTest extends FlatSpec with Matchers {
	def $: HasHP = new HasHP(10)

	it should "throw IllegalArgumentException on zero hp" in {
		an[IllegalArgumentException] should be thrownBy {new HasHP(0)}
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


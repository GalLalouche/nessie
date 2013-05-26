package tests

import org.scalatest.Finders
import org.scalatest.FlatSpec
import org.scalatest.Suite
package object common {
	type ??? = Nothing	
}

trait MySpecs extends Suite {
	val should: ShouldVerb = ???
	val a = this;
}

trait Noun {
	def apply(s: ShouldVerb): ShouldVerb = throw new Error
}

class ShouldVerb {
	def produce(x: Any): ShouldVerb = throw new Error
	def in(x: => Any) = ???
}

class SpecTests extends FlatSpec with MySpecs {
	val EmptySet: Noun = ???

	this EmptySet should produce 3 in {
		throw new Error
	}
}
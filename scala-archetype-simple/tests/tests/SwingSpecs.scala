package tests

import org.scalatest.matchers.{ MatchResult, Matcher, ShouldMatchers }
import scala.swing.{ Frame, Publisher }

//def forAll[T](right: T => Boolean) = new Matcher[GenTraversable[T]] {
//override def apply(left: GenTraversable[T]) = {
//val leftPretty = left.take(3) + {
//if (left.size > 3) "..." else ""
//}
//MatchResult(left.forall(right),
//right + " does not apply to all of " + leftPretty,
//right + " applies too all of " + leftPretty)
//}
//}

trait SwingSpecs extends ShouldMatchers {
	trait Verb;
	def publishOn(f: () => Any) = new Matcher[Publisher] {
		override def apply(left: Publisher) = {
			var didPublish = false;
			val x = new Frame() {
				listenTo(left);
				reactions += {
					case _ => didPublish = true;
				}
			}
			f()
			MatchResult(didPublish,
				left + " did not publish",
				left + " did publish")
		}
	}
	implicit def richEvaluation(r: ResultOfEvaluatingApplication) = new {
		def publish = throw new Error
		def should(p: Verb) = throw new Error
	}
}

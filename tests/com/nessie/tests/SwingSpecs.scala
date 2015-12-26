//package com.nessie.tests
//
//import org.scalatest.matchers.{MatchResult, Matcher, ShouldMatchers}
//import scala.swing.{Frame, Publisher}
//import scala.swing.event.Event
//
////def forAll[T](right: T => Boolean) = new Matcher[GenTraversable[T]] {
////override def apply(left: GenTraversable[T]) = {
////val leftPretty = left.take(3) + {
////if (left.size > 3) "..." else ""
////}
////MatchResult(left.forall(right),
////right + " does not apply to all of " + leftPretty,
////right + " applies too all of " + leftPretty)
////}
////}
//
//trait SwingSpecs extends ShouldMatchers {
//
//	trait Verb
//
//	def publishOn(f: () => Any) = new Matcher[Publisher] {
//		override def apply(left: Publisher) = {
//			var passed = false
//			val x = new Frame() {
//				listenTo(left)
//				reactions += {
//					case _ => passed = true
//				}
//			}
//			f()
//			MatchResult(passed,
//				left + " did not publish",
//				left + " published ")
//		}
//	}
//
//	def publish[T](e: T) = new {
//		def on(f: () => Any) = {
//			new Matcher[Publisher] {
//				override def apply(left: Publisher) = {
//					var passed = false
//					var didPublish: Event = null
//					val x = new Frame() {
//						listenTo(left)
//						reactions += {
//							case `e` => passed = true
//							case other => didPublish = other
//						}
//					}
//					f()
//					MatchResult(passed,
//						left + " did not publish " + e + (if (didPublish != null) "; did publish " + didPublish else ""),
//						left + " published " + e)
//				}
//			}
//		}
//	}
//
//	implicit def richEvaluation(r: ResultOfEvaluatingApplication) = new {
//		def publish = throw new Error
//
//		def should(p: Verb) = throw new Error
//	}
//}

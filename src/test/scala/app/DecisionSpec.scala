package app

import org.scalatest.{FlatSpec, Matchers}

import scala.util.{Failure, Success}

class DecisionSpec extends FlatSpec with Matchers{
  behavior of "Decision"

  it should "return Success of true for T" in {
    val decision = Decision.parse("T")
    decision shouldBe a [Success[Decision]]
    decision.get shouldBe a [Decision]
    decision.get.decision should matchPattern {
      case true =>
    }
  }
  it should "return Success of false for F" in {
    val decision = Decision.parse("F")
    decision shouldBe a [Success[Decision]]
    decision.get shouldBe a [Decision]
    decision.get.decision should matchPattern {
      case false =>
    }
  }
  it should "return Failure for random string" in {
    val decision = Decision.parse("abc")
    decision shouldBe a [Failure[_]]
  }
}

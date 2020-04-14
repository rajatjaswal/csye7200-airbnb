package app

import org.scalatest.{FlatSpec, Matchers}

import scala.util.{Failure, Success}

class PopularAreaSpec extends FlatSpec with Matchers {
  behavior of "PopularArea Parse"

  it should "return Success of true for valid " in {
    val popularAreaString ="Royal Exhibition Building,-37.8047,144.9717,3532"
    val seqPopularArea = popularAreaString.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1)
    val popularArea = PopularArea.parse(seqPopularArea)
    popularArea shouldBe a [Success[PopularArea]]
  }
  it should "return Failure for invalid Data" in {
    val seqListing = Seq("abc", "basud", "asd")
    val popularArea = PopularArea.parse(seqListing)
    popularArea shouldBe a [Failure[_]]
  }
}

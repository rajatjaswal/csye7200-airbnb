package app

import akka.actor.Status.Success
import org.scalatest.{FlatSpec, Matchers}

import scala.util.{Failure, Try}

class CoordinatesSpec extends FlatSpec with Matchers{
  behavior of "Coordinates"

  it should "return Coordinates if proper coordinates are supplied" in {
    val coordinatesString = "-32.456,145.674"
    val coordinates = Coordinates.parse(Helper.elements(coordinatesString.split(",",-1).toSeq, 0,1))
    coordinates.get should matchPattern {
      case Coordinates(-32.456,145.674) =>
    }
  }

  it should "return Failure if proper coordinates are supplied" in {
    val coordinatesString = "145.674"
    val coordinates = Coordinates.parse(Helper.elements(coordinatesString.split(",",-1).toSeq, 0))
    coordinates shouldBe a [Failure[_]]
  }
}

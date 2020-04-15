package app

import org.scalatest.{FlatSpec, Matchers}

import scala.util.{Failure, Try}

class HouseAddressSpec extends FlatSpec with Matchers{
  behavior of "HouseAddress"

  it should "parse a valid HouseAddress string with all properties" in {
    val addressCsvString = "Ashwood,3/5 Yunki Ct,2,t,770000,SP,Buxton,8/04/2017,12.2,3147,2,1,2,162,,2010,Monash City Council,-37.871,145.0994,Southern Metropolitan,2894"
    val ws = addressCsvString.split(",").toSeq
    val houseAddress = HouseAddress.parse(ws, HelperSpec.getPopularAreas());
    houseAddress shouldBe a [Try[HouseAddress]]
    houseAddress.get shouldBe a [HouseAddress]
    (houseAddress.get.longitude, houseAddress.get.latitude) should matchPattern {
      case (145.0994, -37.871) =>
    }
  }
  it should "return Failure for invalid length of parsed csv line" in {
    val addressCsvString = "Ashwood,3/5 Yunki Ct,2,t,770000,SP,Buxton,8/04/2017,-37.871,145.0994,Southern Metropolitan,2894"
    val ws = addressCsvString.split(",").toSeq
    val houseAddress = HouseAddress.parse(ws, HelperSpec.getPopularAreas());
    houseAddress shouldBe a [Failure[_]]
  }
}

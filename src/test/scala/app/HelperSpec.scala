package app

import org.scalatest.{FlatSpec, Matchers}

import scala.util.Try
import app.Helper.{computeHouseNightlyPrice, injectIsWithinPopular}

object HelperSpec {
  val pop1 =  PopularArea(Coordinates(-37.864,144.982), "Area1", 123)
  val pop2 =  PopularArea(Coordinates(-37.8098,144.9652), "Area2", 342)
  val pop3 =  PopularArea(Coordinates(-37.8047,144.9717), "Area3", 871)

  def getPopularArea(): PopularArea = {
    pop1
  }

  def getPopularAreas(): Seq[Try[PopularArea]] = {
    Seq(Try(pop1), Try(pop2), Try(pop3))
  }


  val listing = Listing(ListingAddress("l1"),-37.864,144.982,167,1,10,20, 30, 30, 1, 0.5, 20, 1, 40, null)
}

class HelperSpec extends FlatSpec with Matchers {
  behavior of "Helper.computeHouseNightlyPrice"

  it should "return different house nightly prices for different total price" in {
    val prices = Seq("65000","135000","300000","450000","850000","1950000","4500000")
    val expectedNightlyPrice = Seq(59,92,164,205,232,356,616)
    prices.foreach( p => {
      assert(computeHouseNightlyPrice(p)==expectedNightlyPrice(prices.indexOf(p)))
    })
  }

  behavior of "Helper.injectIsWithinPopular"

  it should """return new listings with popularArea field Injected in them"""  in
    {
      val lat1 = -37.866
      val long1 = 144.990
      val listing1 = Try(HelperSpec.listing.copy(latitude = -37.866, longitude = 144.990))
      val listing2 = Try(HelperSpec.listing.copy(latitude = -37.855, longitude = 144.100))
      val listings = injectIsWithinPopular(HelperSpec.getPopularAreas(), Seq(listing1, listing2))
      assert(listings.head.get.isWithinPopular == 1)
      assert(listings.tail.head.get.isWithinPopular == 0)
    }
}
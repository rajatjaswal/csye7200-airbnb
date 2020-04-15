package app

import org.scalatest.{FlatSpec, Matchers}

import scala.util.Try
import app.Helper.{computeHouseNightlyPrice, injectIsWithinPopular}

object HelperSpec {
  val pop1 =  PopularArea(Coordinates(-37.864,144.982), "Area1", 123)
  val pop2 =  PopularArea(Coordinates(-37.8098,144.9652), "Area2", 342)
  val pop3 =  PopularArea(Coordinates(-37.8047,144.9717), "Area3", 871)

  val listing1 = Listing.parse("19490757,59845433,Westmeadows,Hume,Westmeadows,VIC,3049,-37.67656153,144.873996,House,Private room,2,1,1,65,,,,0,1,0,1,1125,4 weeks ago,t,0,16,40,311,253,98,10,10,10,10,10,10,8,14.54".split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1))
  val listing2 = Listing.parse("5552321,28789055,Melbourne,Melbourne,Melbourne,VIC,3003,-37.82275411,144.9635545,Apartment,Entire home/apt,6,2,2,188,,,149,45,4,10,1,1125,5 days ago,t,11,39,63,147,360,96,10,9,10,10,10,10,9,11.18".split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1))

  val address1 = HouseAddress.parse("Abbotsford,121/56 Nicholson St,2,u,,PI,Biggin,7/11/2016,2.5,3067,2,2,1,4292,82,2009,Yarra City Council,-37.8078,144.9965,Northern Metropolitan,4019".split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1))
  val address2 = HouseAddress.parse("Altona,2/6 Galvin St,3,t,650000,SP,Williams,12/11/2016,13.8,3018,,,,,,,Hobsons Bay City Council,,,Western Metropolitan,5301".split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1))

  def getAddresses(): Seq[Try[HouseAddress]] = {
    Seq(address1, address2)
  }

  def getPopularArea(): PopularArea = {
    pop1
  }

  def getListings(): Seq[Try[Listing]] = {
    Seq(listing1,listing2)
  }

  def getPopularAreas(): Seq[Try[PopularArea]] = {
    Seq(Try(pop1), Try(pop2), Try(pop3))
  }


  val listing = Listing(ListingAddress("l1"),-37.864,144.982,167,1,10,20, 30, 30, 1, 0.5, 20, 1, 40, null, 1)
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
      assert(listings.head.get.isWithinPopular == 0)
      assert(listings.tail.head.get.isWithinPopular == 0)
    }
}
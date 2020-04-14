package app

import org.scalatest.{FlatSpec, Matchers}

import scala.util.{Failure, Success}

class ListingSpec extends FlatSpec with Matchers{

  "Listing" should """return true if is withinMileOfArea of a PopularArea of same coordinates """  in
    {
      val lat1 = -37.864
      val lat2 = 144.982
      val listing = HelperSpec.listing.copy(latitude = lat1, longitude = lat2)
      val tuple = listing.isWithinMileOfArea(1, HelperSpec.pop1)
      tuple should matchPattern {
        case (true,0.0) =>
      }
    }

  it should """return true if is withinMileOfArea of a PopularArea of different coordinates but within 1 mile"""  in
  {
    val lat1 = -37.861
    val lat2 = 144.989
    val listing = HelperSpec.listing.copy(latitude = lat1, longitude = lat2)
    val tuple = listing.isWithinMileOfArea(1, HelperSpec.pop1)
    tuple should matchPattern {
      case (true, _) =>
    }
    assert(tuple._2 < 1)
  }

  it should """return false if is withinMileOfArea of a PopularArea of different coordinates and outside 1 mile"""  in
  {
    val lat1 = -37.961
    val lat2 = 144.989
    val listing = HelperSpec.listing.copy(latitude = lat1, longitude = lat2)
    val tuple = listing.isWithinMileOfArea(1, HelperSpec.pop1)
    tuple should matchPattern {
      case (false, _) =>
    }
    assert(tuple._2 > 1)
  }

  it should """return closest PopularArea if there are multiple within 1 mile """  in
  {
    val lat1 = -37.866
    val lat2 = 144.990
    val listing = HelperSpec.listing.copy(latitude = lat1, longitude = lat2)
    val tuple = listing.hasClosestPopularArea(1, listing, HelperSpec.getPopularAreas())
    tuple should matchPattern {
      case (_,1, _) =>
    }
    assert(tuple._1.place == "Area1")
  }

  it should """return null as closestPopularArea if there is none closest PopularArea within 1 mile """  in
  {
    val lat1 = -38.900
    val lat2 = 156.990
    val listing = HelperSpec.listing.copy(latitude = lat1, longitude = lat2)
    val tuple = listing.hasClosestPopularArea(1, listing, HelperSpec.getPopularAreas())
    tuple should matchPattern {
      case (null,0, _) =>
    }
  }

  behavior of "Listing Parse"

  it should "return Success of true for valid " in {
    val listingString ="19490757,59845433,Westmeadows,Hume,Westmeadows,VIC,3049,-37.67656153,144.873996,House,Private room,2,1,1,65,,,,0,1,0,1,1125,4 weeks ago,t,0,16,40,311,253,98,10,10,10,10,10,10,8,14.54"
    val seqListing = listingString.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1)
    val listing = Listing.parse(seqListing)
    listing shouldBe a [Success[ListingAddress]]
  }
  it should "return Failure for invalid Data" in {
    val seqListing = Seq("abc", "basud", "asd")
    val listing = Listing.parse(seqListing)
    listing shouldBe a [Failure[_]]
  }
}

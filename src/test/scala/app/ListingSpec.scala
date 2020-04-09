package app

import org.scalatest.{FlatSpec, Matchers}

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
}

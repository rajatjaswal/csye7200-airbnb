package app

object Airbnb{

}

case class Airbnb(listings: Seq[Listing])

case class Listing(id:Int, listingUrl: String, name: String, coordinates: Coordinates)

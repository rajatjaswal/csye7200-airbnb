package app

import scala.util.{Failure, Success, Try}
import Helper._
import ingestion.Ingestible

object Listing {

  trait IngestibleListing extends Ingestible[Listing] {
    def fromString(w: String): Try[Listing] = Listing.parse(w.split(",").toSeq)
  }

  def parse(ws: Seq[String]):Try[Listing] ={
    val listingId = Try(ws(0).toLong).getOrElse(0L)
    val hostId = Try(ws(1).toLong).getOrElse(0L)
    val address = ListingAddress.parse(elements(ws, 2,8,10))
    val coordinates= Coordinates.parse(elements(ws, 12, 13))
    println(listingId)
    val accommodates = Try(ws(17).toInt).getOrElse(0)
    val bedrooms = Try(ws(19).toInt).getOrElse(0)
    val price = Try(ws(21).toLong).getOrElse(0L)
    val number_of_reviews = Try(ws(36).toInt).getOrElse(0)
    val review_scores_rating = Try(ws(37).toDouble).getOrElse(0.0)
    val average_service_review_scores = Try(((ws(38).toInt + ws(39).toInt + ws(40).toInt + ws(41).toInt + ws(42).toInt + ws(43).toInt)/60).toDouble).getOrElse(0.0)
    val reviews_per_month = Try(ws(45).toDouble).getOrElse(0.0)

    import Function._
    val fy = lift2(uncurried2((apply _).curried))
    for(f <- fy(coordinates, address)) yield f(listingId)(hostId)(accommodates)(bedrooms)(price)(number_of_reviews)(review_scores_rating)(average_service_review_scores)(reviews_per_month)
  }

  def addressesWithinMile(mile: Double): Seq[Listing] = ???
}
case class Listing(coordinates: Coordinates, address: ListingAddress, listingId: Long, hostId: Long, accommodates: Int, bedrooms: Int, price: Long, number_of_reviews: Int, review_scores_rating: Double, average_service_review_scores: Double, reviews_per_month: Double){

}

object ListingAddress {
  def parse(params: List[String]): Try[ListingAddress] = {
    val street = params(0)
     street match  {
      case "" => Failure(new Exception(s"No street address found"))
      case _ => Success(apply(street+" "+params(1).substring(1)+" "+params(2)))
    }
  }
}

case class ListingAddress(address: String){

}
package app

import scala.util.{Failure, Success, Try}
import Helper._
import ingestion.Ingestible
import org.apache.spark.rdd.RDD

object Listing {

  trait IngestibleListing extends Ingestible[Listing] {
    def fromString(w: String): Try[Listing] = Listing.parse(w.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1).toSeq)
  }

  def parse(ws: Seq[String]):Try[Listing] ={
    if(!ws.length.equals(39)){
      return Failure(new Exception("Invalid Data"));
    }

    val listingId = Try(ws.head.toLong).getOrElse(0L)
    val hostId = Try(ws(1).toLong).getOrElse(0L)
    val address = ListingAddress.parse(elements(ws, 2))
    val coordinates= Coordinates.parse(elements(ws, 7, 8))
    val accommodates = Try(ws(11).toInt).getOrElse(0)
    val bedrooms = Try(ws(12).toInt).getOrElse(0)
    val price = Try(ws(14).toLong).getOrElse(0L)
    val number_of_reviews = Try(ws(29).toInt).getOrElse(0)
    val review_scores_rating = Try(ws(30).toDouble).getOrElse(0.0)
    val average_service_review_scores = Try(((ws(31).toInt + ws(32).toInt + ws(33).toInt + ws(36).toInt + ws(34).toInt + ws(35).toInt)/6).toDouble).getOrElse(0.0)
    val reviews_per_month = Try(ws(38).toDouble).getOrElse(0.0)
    val isWithinMile=0
    val minDistance: Double=0D
    val closestPopularArea: PopularArea = null

    import Function._
    val fy = lift(uncurried((apply _).curried))
    for(f <- fy(address)) yield f(coordinates.get.lat)(coordinates.get.longitude)(listingId)(hostId)(accommodates)(bedrooms)(price)(number_of_reviews)(review_scores_rating)(average_service_review_scores)(reviews_per_month)(isWithinMile)(minDistance)(closestPopularArea)
  }

  def addressesWithinMile(mile: Double): Seq[Listing] = ???
}
case class Listing(address: ListingAddress, latitude: Double, longitude: Double, listingId: Long, hostId: Long, accommodates: Int, bedrooms: Int, price: Long, number_of_reviews: Int, review_scores_rating: Double, average_service_review_scores: Double, reviews_per_month: Double, isWithinPopular: Int, minDistance: Double, closestPopularArea: PopularArea){
  def hasClosestPopularArea(mile: Double, listing: Listing, popularAreas: Seq[Try[PopularArea]]): (PopularArea, Int, Double) ={
    val closestPopularAreas = popularAreas.map{
      case Success(pop) => {
        val isWithin = isWithinMileOfArea(mile, pop);
        (pop, if(isWithin._1) 1 else 0, isWithin._2)
      }
    }.filter(_._2==1)

    if(closestPopularAreas.isEmpty) (null, 0, 0D)
    else closestPopularAreas.minBy(_._3)
  }

  def isWithinMileOfArea(mile: Double, popularArea: PopularArea): (Boolean, Double) = {
    val lat1 = math.toRadians(latitude)
    val long1 =  math.toRadians(longitude)
    val lat2 = math.toRadians(popularArea.coordinates.lat)
    val long2 = math.toRadians(popularArea.coordinates.longitude)

    val dlon = long2 - long1;
    val dlat = lat2 - lat1;
    val a = math.pow(math.sin(dlat / 2), 2) + math.cos(lat1) * math.cos(lat2) * math.pow(math.sin(dlon / 2),2);

    val c = 2 * math.asin(math.sqrt(a))

    val r:Double = 3956

    (c*r <= mile, c*r)
  }
}

object ListingAddress {
  def parse(params: List[String]): Try[ListingAddress] = {
    val street = params(0)
     street match  {
      case "" => Failure(new Exception(s"No street address found"))
      case _ => Success(apply(street))
    }
  }
}

case class ListingAddress(address: String){

}
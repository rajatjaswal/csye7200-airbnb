package app

import scala.collection.mutable
import scala.util.{Success, Try}

object Helper {

  val mile = 3.5
  def elements(list: Seq[String], indices: Int*): List[String] = {
    val x = mutable.ListBuffer[String]()
    for (i <- indices) x += list(i)
    x.toList
  }

  def injectIsWithinPopular(popAreas: Seq[Try[PopularArea]], listings: Seq[Try[Listing]]): Seq[Try[Listing]] = {
    listings.map(l => {
      for (lx <- l; (popArea, isBoolean, minDistance)  = lx.hasClosestPopularArea(lx, popAreas)) yield lx.copy(isWithinPopular = isBoolean, minDistance = minDistance , closestPopularArea = popArea)
    })
  }

  def computeHouseNightlyPrice(priceString: String): Long = {
    val base = 50000
    val price = Try(priceString.toDouble).getOrElse(0.0)
    val yearlyPrice = price/365
    if(price <= base)  (yearlyPrice/3).toLong
    else if(base<price && price<= base*2 )  (yearlyPrice/4).toLong
    else if(base*2<price && price<= base*4)  (yearlyPrice/5).toLong
    else if(base*4<price && price<= base*8) (yearlyPrice/6).toLong
    else if(base*8<price && price<= base*16) (yearlyPrice/7).toLong
    else if(base*16< price && price<= base*32) (yearlyPrice/12).toLong
    else if(base*32< price && price<= base*64) (yearlyPrice/18).toLong
    else (yearlyPrice/25).toLong
  }

  def calculatePopularAreas(popularAreas: Seq[Try[PopularArea]], latitude: Double, longitude: Double) = {
    val closestPopularAreas = popularAreas.map{
      case Success(pop) => {
        val isWithin = isWithinMileOfArea(pop, latitude, longitude);
        (pop, if(isWithin._1) 1 else 0, isWithin._2)
      }
    }.filter(_._2==1)

    closestPopularAreas
  }

  def isWithinMileOfArea(popularArea: PopularArea, latitude: Double, longitude: Double): (Boolean, Double) = {
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

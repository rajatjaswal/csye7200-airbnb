package app

import scala.util.{Failure, Try}
import Helper._
import ingestion.Ingestible

object HouseAddress {

  def parse(ws: Seq[String], popularAreas:Seq[Try[PopularArea]]):Try[HouseAddress] ={
    if(!ws.length.equals(21)){
      return Failure(new Exception(s"Invalid Data"))
    }
    val address = ws(1)
    val rooms = Try(ws(2).toInt).getOrElse(0)
    val price= computeHouseNightlyPrice(ws(4))
    val landSize=Try(ws(13).toLong).getOrElse(0L)
    val coordinates = Coordinates.parse(elements(ws, 17,18))
    val isHotel: Boolean = rooms >=5
    val availability: Boolean = true
    val decision = Decision.parse("F")
    val closestPopularAreas = Helper.calculatePopularAreas(popularAreas, coordinates.get.lat, coordinates.get.longitude)
    val isWithinPopularArea= if(closestPopularAreas.isEmpty) 0 else 1
    import Function._
    val fy = lift(uncurried((apply _).curried))
    for(f <- fy(decision)) yield f(coordinates.get.lat)(coordinates.get.longitude)(price)(landSize)(address)(rooms)(isHotel)(availability)(isWithinPopularArea)
  }
}
case class HouseAddress(decision: Decision, latitude: Double, longitude: Double, price: Long, landSize: Long, address: String, rooms: Int, isHotel: Boolean, availability: Boolean, isWithinPopularArea: Int){

}
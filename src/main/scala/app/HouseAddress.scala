package app

import scala.util.{Failure, Try}
import Helper._
import ingestion.Ingestible

object HouseAddress {

  trait IngestibleHouseAddress extends Ingestible[HouseAddress] {
    def fromString(w: String): Try[HouseAddress] = HouseAddress.parse(w.split(",").toSeq)
  }

  def parse(ws: Seq[String]):Try[HouseAddress] ={
    val address = ws(1)
    val rooms = Try(ws(2).toInt).getOrElse(0)
    val price= computeHouseNightlyPrice(ws(4))
    val landSize=Try(ws(13).toLong).getOrElse(0L)
    val coordinates = Coordinates.parse(elements(ws, 17,18))
    val isHotel: Boolean = rooms >=5
    val availability: Boolean = true
    val decision = Decision.parse("F")

    import Function._
    val fy = lift(uncurried((apply _).curried))
    for(f <- fy(decision)) yield f(coordinates.get.lat)(coordinates.get.longitude)(price)(landSize)(address)(rooms)(isHotel)(availability)
  }

  def addressesWithinMile(mile: Double): Seq[HouseAddress] = ???
}
case class HouseAddress(decision: Decision, latitude: Double, longitude: Double, price: Long, landSize: Long, address: String, rooms: Int, isHotel: Boolean, availability: Boolean){

}
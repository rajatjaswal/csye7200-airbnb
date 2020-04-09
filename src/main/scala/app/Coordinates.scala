package app

import scala.util.{Failure, Success, Try}

object Coordinates{
  def parse(params: List[String]): Try[Coordinates] = params match {
    case lat :: longitude :: Nil => Success(apply(Try(lat.toDouble).getOrElse(0), Try(longitude.toDouble).getOrElse(0)))
    case _ => Failure(throw new Exception(s"Logic error in processing coordinates"))
  }
}
case class Coordinates(lat: Double, longitude: Double){
}

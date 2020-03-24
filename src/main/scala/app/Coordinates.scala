package app

import scala.util.{Failure, Success, Try}

object Coordinates{
  def parse(params: List[String]): Try[Coordinates] = params match {
    case lat :: long :: Nil => Success(apply(lat, long))
    case _ => Failure(new Exception(s"logic error in Coordinates: $params"))
  }
}
case class Coordinates(lat: String, long: String){
}

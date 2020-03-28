package app

import scala.util.{Failure, Success, Try}

object Coordinates{
  def parse(params: List[String]): Try[Coordinates] = params match {
    case lat :: long :: Nil => Try(apply(lat.toDouble, long.toDouble))
    case _ => Failure(new Exception(s"logic error in Coordinates: $params"))
  }
}
case class Coordinates(lat: Double, long: Double){
}

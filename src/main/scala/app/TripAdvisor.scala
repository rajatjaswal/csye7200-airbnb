package app

import scala.collection.mutable
import scala.util.Try

object TripAdvisor {
  val top10PopularArea:List[Area] = ???
  val totalPopularAreas:List[Area] = ???

//  def parse(ws: Seq[String]):Try[TripAdvisor] ={
//
//  }
}
case class TripAdvisor(popularAreas: List[Area])

case class Area(coordinates: Coordinates, rating: Int)

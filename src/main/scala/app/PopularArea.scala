package app

import app.Helper.elements
import ingestion.Ingestible

import scala.util.{Failure, Try}

object PopularArea {
  trait IngestiblePopularArea extends Ingestible[PopularArea] {
    def fromString(w: String): Try[PopularArea] = PopularArea.parse(w.split(",").toSeq)
  }

  def parse(ws: Seq[String]):Try[PopularArea] ={

    if(!ws.length.equals(4)){
      return Failure(new Exception("Invalid Data"));
    }

    val place = ws.head
    val coordinates= Coordinates.parse(elements(ws, 1, 2))
    val reviews=ws(3).toInt

    import Function._
    val fy = lift(uncurried((apply _).curried))
    for(f <- fy(coordinates)) yield f(place)(reviews)
  }
}

case class PopularArea(coordinates: Coordinates, place: String, rating: Int) {

}

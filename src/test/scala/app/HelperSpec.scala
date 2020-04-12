package app

import scala.util.Try

object HelperSpec {
  val pop1 =  PopularArea(Coordinates(-37.864,144.982), "Area1", 123)
  val pop2 =  PopularArea(Coordinates(-37.8098,144.9652), "Area2", 342)
  val pop3 =  PopularArea(Coordinates(-37.8047,144.9717), "Area3", 871)

  def getPopularArea(): PopularArea = {
    pop1
  }

  def getPopularAreas(): Seq[Try[PopularArea]] = {
    Seq(Try(pop1), Try(pop2), Try(pop3))
  }


  val listing = Listing(ListingAddress("l1"),-37.864,144.982,167,1,10,20, 30, 30, 1, 0.5, 20, 1, 40, null)
}

package app

import scala.collection.mutable
import scala.util.Try

object Helper {
  def elements(list: Seq[String], indices: Int*): List[String] = {
    val x = mutable.ListBuffer[String]()
    for (i <- indices) x += list(i)
    x.toList
  }

  def injectIsWithinPopular(popAreas: Seq[Try[PopularArea]], listings: Seq[Try[Listing]]): Seq[Try[Listing]] = {
    val mile = 1
    listings.map(l => {
      for (lx <- l; (popArea, isBoolean, minDistance)  = lx.hasClosestPopularArea(mile, lx, popAreas)) yield lx.copy(isWithinPopular = isBoolean, minDistance = minDistance , closestPopularArea = popArea)
    })
  }
}

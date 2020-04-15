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
    val mile = 3.0
    listings.map(l => {
      for (lx <- l; (popArea, isBoolean, minDistance)  = lx.hasClosestPopularArea(mile, lx, popAreas)) yield lx.copy(isWithinPopular = isBoolean, minDistance = minDistance , closestPopularArea = popArea)
    })
  }

  def computeHouseNightlyPrice(priceString: String): Long = {
    val base = 50000
    val price = Try(priceString.toDouble).getOrElse(0.0)
    val yearlyPrice = price/365
    if(price <= base)  (yearlyPrice/2).toLong
    else if(base<price && price<= base*2 )  (yearlyPrice/3).toLong
    else if(base*2<price && price<= base*4)  (yearlyPrice/4).toLong
    else if(base*4<price && price<= base*8) (yearlyPrice/5).toLong
    else if(base*8<price && price<= base*16) (yearlyPrice/6).toLong
    else if(base*16< price && price<= base*32) (yearlyPrice/10).toLong
    else if(base*32< price && price<= base*64) (yearlyPrice/15).toLong
    else (yearlyPrice/20).toLong
  }
}

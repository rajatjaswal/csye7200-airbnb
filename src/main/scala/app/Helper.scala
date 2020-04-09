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
    val mile = 1.5
    listings.map(l => {
      for (lx <- l; (popArea, isBoolean, minDistance)  = lx.hasClosestPopularArea(mile, lx, popAreas)) yield lx.copy(isWithinPopular = isBoolean, minDistance = minDistance , closestPopularArea = popArea)
    })
  }

  def computeHouseNightlyPrice(priceString: String): Long = {
    val base = 50000
    val price = Try(priceString.toDouble/365).getOrElse(0.0)
    if(0 until base contains price)  (price/2).toLong
    else if(base until base*2 contains price)  (price/3).toLong
    else if(base*2 until base*4 contains price)  (price/4).toLong
    else if(base*4 until base*8 contains price) (price/5).toLong
    else if(base*8 until base*16 contains price) (price/6).toLong
    else if(base*16 until base*32 contains price) (price/10).toLong
    else if(base*32 until base*64 contains price) (price/15).toLong
    else (price/20).toLong
  }
}

package app

import app.HouseAddress.IngestibleHouseAddress
import app.Listing.IngestibleListing
import app.PopularArea.IngestiblePopularArea
import ingestion.Ingest
import spark.SparkConnector

import scala.io.{Codec, Source}
import scala.util.Try
import Helper.injectIsWithinPopular
import backend.WebServer

object Main extends App{
  implicit object IngestibleHouseAddress extends IngestibleHouseAddress
  implicit object IngestibleListing extends IngestibleListing
  implicit object IngestiblePopularArea extends IngestiblePopularArea
  val address_ingester = new Ingest[HouseAddress]()
  val listing_ingester = new Ingest[Listing]()
  val popularArea_ingestor = new Ingest[PopularArea]()
  if (args.length > 1) {
    implicit val codec = Codec.UTF8
    val address_source = Source.fromResource(args(0))
    val listing_source = Source.fromResource(args(1))
    val popularArea_source = Source.fromResource(args(2))
    val addresses:Seq[Try[HouseAddress]] = address_ingester(address_source).toSeq
    val listings:Seq[Try[Listing]] = listing_ingester(listing_source).toSeq
    val popularAreas:Seq[Try[PopularArea]] = popularArea_ingestor(popularArea_source).toSeq
    val listingsInjected:Seq[Try[Listing]] = injectIsWithinPopular(popularAreas, listings)
//    println(addresses);
    val xs=listingsInjected.filter(l => l.get.isWithinPopular==1)
    println(listings.length)
    println(xs.length)
//    println(listingsInjected.toList)
    println(popularAreas)
    listing_source.close()
    popularArea_source.close()

    SparkConnector.createNewSparkServer(listingsInjected)
    WebServer.initialize(addresses);

    address_source.close()
  }
}

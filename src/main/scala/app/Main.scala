package app

import app.HouseAddress.IngestibleHouseAddress
import app.Listing.IngestibleListing
import app.PopularArea.IngestiblePopularArea
import ingestion.Ingest
import spark.SparkConnector

import scala.io.{Codec, Source}
import scala.util.{Failure, Try}
import Helper.injectIsWithinPopular
import akka.actor.ActorSystem
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
    val cleansed_addresses = addresses.filter(a => a.get.coordinates!=Coordinates(0.0,0.0))
    println(cleansed_addresses);
    println(listings.length)
    println(xs.length)
//    println(listingsInjected.toList)
    println(popularAreas)
    listing_source.close()
    popularArea_source.close()

    implicit val system = ActorSystem("my-system")
    WebServer.initialize(cleansed_addresses, listingsInjected, popularAreas);
    SparkConnector.createNewSparkServer(listingsInjected)

    address_source.close()
  }
}

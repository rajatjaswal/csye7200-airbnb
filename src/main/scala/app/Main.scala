package app

import app.HouseAddress.IngestibleHouseAddress
import app.Listing.IngestibleListing
import ingestion.Ingest

import scala.io.{Codec, Source}
import scala.util.Try

object Main extends App{

  implicit object IngestibleHouseAddress extends IngestibleHouseAddress
  implicit object IngestibleListing extends IngestibleListing
  val address_ingester = new Ingest[HouseAddress]()
  val listing_ingester = new Ingest[Listing]()
  if (args.length > 1) {
    implicit val codec = Codec.UTF8
    val address_source = Source.fromResource(args(0))
    val listing_source = Source.fromResource(args(1))
    val addresses:Iterator[Try[HouseAddress]] = address_ingester(address_source)
    val listings:Iterator[Try[Listing]] = listing_ingester(listing_source)
    println(addresses.toSeq.take(2));
    println(listings.toSeq.take(2));
    address_source.close()
    listing_source.close()
  }
}

package app

import app.HouseAddress.IngestibleHouseAddress
import ingestion.Ingest

import scala.io.{Codec, Source}
import scala.util.Try

object Main extends App{

  implicit object IngestibleHouseAddress extends IngestibleHouseAddress

  val ingester = new Ingest[HouseAddress]()
  if (args.length > 0) {
    implicit val codec = Codec.UTF8
    val source = Source.fromResource(args.head)

    val addresses:Iterator[Try[HouseAddress]] = ingester(source)
    println(addresses.toSeq.take(2));
    source.close()
  }
}

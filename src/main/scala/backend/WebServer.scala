package backend

import akka.GetAddreses
import akka.actor.{ActorRef, ActorSystem}
import akka.http.impl.engine.ws.UpgradeToWebSocketResponseHeader
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods.{DELETE, GET, OPTIONS, POST, PUT}
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{`Access-Control-Allow-Credentials`, `Access-Control-Allow-Headers`, `Access-Control-Allow-Methods`, `Access-Control-Allow-Origin`, `Access-Control-Max-Age`}
import akka.http.scaladsl.model.ws.{Message, TextMessage, WebSocketRequest}
import akka.http.scaladsl.server.{Directive0, Route}
import akka.http.scaladsl.server.Directives.{pathPrefix, _}
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.util.Timeout
import app.{HouseAddress, Listing, PopularArea}
import akka.pattern.ask
import spray.json._

import scala.concurrent.{Future, Promise}
import scala.concurrent.duration.DurationLong
import scala.util.Try

object WebServer {


  def getAddressJson(addresses: Seq[Try[HouseAddress]]): String ={
    println(s"Starting serialization of addresses")
    import AddressProtocol._
    val addressSerialize = addresses.flatMap(_.toOption).toJson.prettyPrint
    println(s"Completed address serialization")
    addressSerialize
  }

  def getListingJson(listings: Seq[Try[Listing]]): String ={
    import ListingProtocol._
    val listingsSerialize = listings.flatMap(_.toOption).toJson.prettyPrint
    listingsSerialize
  }

  def getPopularAreaJson(popularAreas: Seq[Try[PopularArea]]): String = {
    import PopularAreaProtocol._
    val popularAreaSerialize = popularAreas.flatMap(_.toOption).toJson.prettyPrint
    popularAreaSerialize
  }

  def wsAddressFlow(wsSource: Source[String, Any]):Flow[Message, Message, _] =
    Flow.fromSinkAndSource(
      Sink.ignore,
      wsSource
        .map(address => {
          import AddressProtocol._
          TextMessage.Strict(address)
        })
    )

  def initialize(addresses: Seq[Try[HouseAddress]], listings: Seq[Try[Listing]], popularAreas: Seq[Try[PopularArea]], actor: ActorRef)(implicit system: ActorSystem) {
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

//    val wsSource = Source(addresses.to[scala.collection.immutable.Iterable])

    val cors = new CORSHandler {}
    val addressSerialize = getAddressJson(addresses)
    val listingsSerialize = getListingJson(listings)
    val popularAreaSerialize = getPopularAreaJson(popularAreas)
    val route: Route =
      pathPrefix("airbnb-service") {
        //Necessary to let the browser make OPTIONS requests as it likes to do
        pathPrefix("addresses") {
          pathEnd {
            concat(
              options {
                cors.corsHandler(complete(StatusCodes.OK))
              },
              get {
                implicit val timeout: Timeout = Timeout(5 seconds)
                val addresses: Future[String] = (actor ? GetAddreses).mapTo[String]
                handleWebSocketMessages(wsAddressFlow(Source.fromFuture(addresses)))
              }
            )
          }
        } ~ pathPrefix("listings"){
          pathEnd{
            concat(
              options {
                cors.corsHandler(complete(StatusCodes.OK))
              },
              get{
                cors.corsHandler (complete(HttpEntity(ContentTypes.`application/json`, listingsSerialize)))
              }
            )
          }
        } ~ pathPrefix("popularArea"){
          pathEnd{
            concat(
              options {
                cors.corsHandler(complete(StatusCodes.OK))
              },
              get{
                cors.corsHandler (complete(HttpEntity(ContentTypes.`application/json`, popularAreaSerialize)))
              }
            )
          }
        }
    }

    val bindingFuture = Http().bindAndHandle(route, "localhost",3700)

    println(s"Addresses online at http://localhost:3700/airbnb-service/addresses\n")
    println(s"Listings online at http://localhost:3700/airbnb-service/listings\n")
    println(s"Listings online at http://localhost:3700/airbnb-service/popularArea\n")
  }

}

trait CORSHandler {

  private val corsResponseHeaders = List(
    `Access-Control-Allow-Origin`.*,
    `Access-Control-Allow-Credentials`(true),
    `Access-Control-Allow-Headers`("Authorization",
      "Content-Type", "X-Requested-With"),
    `Access-Control-Max-Age`(1.day.toMillis)//Tell browser to cache OPTIONS requests
  )
  //this directive adds access control headers to normal responses
  private def addAccessControlHeaders: Directive0 = {
    respondWithHeaders(corsResponseHeaders)
  }
  //this handles preflight OPTIONS requests.
  private def preflightRequestHandler: Route = options {
    complete(HttpResponse(StatusCodes.OK).
      withHeaders(`Access-Control-Allow-Methods`(OPTIONS, POST, PUT, GET, DELETE)))
  }
  // Wrap the Route with this method to enable adding of CORS headers
  def corsHandler(r: Route): Route = addAccessControlHeaders {
    preflightRequestHandler ~ r
  }
  // Helper method to add CORS headers to HttpResponse
  // preventing duplication of CORS headers across code
  def addCORSHeaders(response: HttpResponse):HttpResponse =
    response.withHeaders(corsResponseHeaders)
}

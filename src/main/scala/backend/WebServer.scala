package backend

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods.{DELETE, GET, OPTIONS, POST, PUT}
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{`Access-Control-Allow-Credentials`, `Access-Control-Allow-Headers`, `Access-Control-Allow-Methods`, `Access-Control-Allow-Origin`, `Access-Control-Max-Age`}
import akka.http.scaladsl.server.{Directive0, Route}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.stream.ActorMaterializer
import app.HouseAddress
import spray.json._

import scala.concurrent.duration.DurationLong
import scala.util.Try

object WebServer {


  def initialize(addresses: Seq[Try[HouseAddress]]) {

    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    import AddressProtocol._
    val cors = new CORSHandler {}
    val serialize = addresses.flatMap(_.toOption).toJson.prettyPrint

    val route: Route = path("getAllAddresses") {
      //Necessary to let the browser make OPTIONS requests as it likes to do
      options {
        cors.corsHandler(complete(StatusCodes.OK))
      } ~ get( cors.corsHandler(complete(HttpEntity(ContentTypes.`application/json`, serialize))) )
    }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 3700)

    println(s"Server online at http://localhost:3700/getAllAddresses\n")
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

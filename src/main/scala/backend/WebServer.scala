package backend

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import app.{Coordinates, Decision, HouseAddress}
import spray.json._

import scala.util.Try

object WebServer {


  def initialize(addresses: Seq[Try[HouseAddress]]) {

    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    import AddressProtocol._
    val serialize = addresses.flatMap(_.toOption).toJson.prettyPrint
    val route =
      path("getAllAddresses") {
        get {
          complete(HttpEntity(ContentTypes.`application/json`, serialize))
        }
      }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 3700)

    println(s"Server online at http://localhost:3700/getAllAddresses\n")
  }
}

package backend

import akka.actor.ActorRef
import akka.http.scaladsl.testkit.ScalatestRouteTest
import app.HelperSpec
import org.scalatest.{Matchers, WordSpec}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.Directives._

class BackendSpec extends WordSpec with Matchers with ScalatestRouteTest{

  val popularAreas = HelperSpec.getPopularAreas()
  val listings=HelperSpec.getListings()
  val actor:ActorRef =null // TODO
  val route = WebServer.getRoutes(listings,popularAreas, actor)

  "The Airbnb service" should {

    "return Listings for GET requests to the root path" in {
      // tests:
      Get("/airbnb-service/listings") ~> route ~> check {
        val expected = "[{\n  \"address\": \"Westmeadows\",\n  \"average_price\": 65,\n  \"decision\": 0,\n  \"lat\": -37.67656153,\n  \"long\": 144.873996\n}, {\n  \"address\": \"Melbourne\",\n  \"average_price\": 94,\n  \"decision\": 0,\n  \"lat\": -37.82275411,\n  \"long\": 144.9635545\n}]"
        println(responseAs[String])
        responseAs[String] shouldEqual expected
      }
    }

    "return PopularAreas for GET requests to the root path" in {
      // tests:
      Get("/airbnb-service/popularArea") ~> route ~> check {
        val expected = "[{\n  \"address\": \"Area1\",\n  \"lat\": -37.864,\n  \"long\": 144.982,\n  \"rating\": 123\n}, {\n  \"address\": \"Area2\",\n  \"lat\": -37.8098,\n  \"long\": 144.9652,\n  \"rating\": 342\n}, {\n  \"address\": \"Area3\",\n  \"lat\": -37.8047,\n  \"long\": 144.9717,\n  \"rating\": 871\n}]"
//        println(responseAs[String])
        responseAs[String] shouldEqual expected
      }
    }
  }
}

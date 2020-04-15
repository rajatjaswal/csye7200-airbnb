package backend

import akka.{GetAddreses, HouseAddressActor}
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.testkit.{ImplicitSender, TestActorRef, TestActors, TestKit}
import app.{HelperSpec, HouseAddress}
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers, WordSpec}
import akka.pattern.ask
import akka.util.Timeout

import scala.collection.mutable.Seq
import scala.concurrent.Future
import scala.concurrent.duration.DurationLong
import scala.util.Success

class HouseAddressBackendSpec()
  extends WordSpec
    with Matchers
    with BeforeAndAfterAll
    with ScalatestRouteTest {



  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }


  "The Airbnb service" should {

    "return Listings for GET requests to the root path" in {

      val actor = system.actorOf(Props[HouseAddressActor], "actor")
      val address: Seq[HouseAddress] = HelperSpec.getAddresses().flatMap(_.toOption).to[scala.collection.mutable.Seq]
      val addressString = WebServer.getAddressJson(address)

      actor ! address

      val route = WebServer.getRoutes(Seq.empty, Seq.empty, actor)
      // tests:
      Get("/airbnb-service/addresses") ~> route ~> check {
        responseAs[String] shouldEqual addressString
      }
    }
  }

}

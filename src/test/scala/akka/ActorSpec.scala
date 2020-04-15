package akka

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import akka.util.Timeout
import app.{HelperSpec, HouseAddress}
import backend.WebServer
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}

import scala.collection.mutable.Seq
import scala.concurrent.Future
import scala.util.Success
import akka.pattern.ask

import scala.concurrent.duration.DurationLong

class ActorSpec (_system: ActorSystem)
  extends TestKit(_system)
    with ImplicitSender
    with Matchers
    with FlatSpecLike
    with BeforeAndAfterAll{

  def this() = this(ActorSystem("Actor"))

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "The actor" should  "send back messages unchanged" in {
    val actor = system.actorOf(Props[HouseAddressActor], "actor")
    val address:Seq[HouseAddress] = HelperSpec.getAddresses().flatMap(_.toOption).to[scala.collection.mutable.Seq]
    val addressString = WebServer.getAddressJson(address)

    actor ! address
    expectMsg(address)
  }

  "The actor" should  "send Invalid for invalid message" in {
    val actor = system.actorOf(Props[HouseAddressActor], "actor-2")

    actor ! "Hello"

    expectMsg("Invalid")
  }

}

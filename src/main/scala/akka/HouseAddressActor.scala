package akka;

import akka.actor.Actor
import app.HouseAddress
import backend.WebServer

import scala.collection.mutable.ListBuffer
import scala.util.Try;
import scala.collection.mutable.Seq
import collection.mutable.Buffer
case object GetAddreses;
class HouseAddressActor extends Actor {
  var addresses:Seq[HouseAddress] = Seq.empty
  var len:ListBuffer[Int] = ListBuffer.empty;
  def receive = {
    case newAddress :Seq[HouseAddress] => {
      addresses = newAddress
      sender() ! newAddress
    }
    case GetAddreses => {
      sender() ! WebServer.getAddressJson(addresses)
    }

    case _ => {
      sender() ! "Invalid"
    }
  }
}

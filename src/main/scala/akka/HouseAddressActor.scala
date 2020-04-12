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
  var addresses:Seq[Try[HouseAddress]] = Seq.empty
  var len:ListBuffer[Int] = ListBuffer.empty;
  def receive = {
    case newAddress :Seq[Try[HouseAddress]] => {
      addresses = newAddress
    }
    case GetAddreses => {
      sender() ! WebServer.getAddressJson(addresses)
    }
    case x:Int => {
      len += x
    }
    case _ => "Invalid"
  }
}

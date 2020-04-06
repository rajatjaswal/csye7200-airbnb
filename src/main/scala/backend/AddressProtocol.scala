package backend

import app.{Coordinates, Decision, HouseAddress}
import spray.json._

object AddressProtocol extends DefaultJsonProtocol {

  implicit object houseAddressFormat extends RootJsonFormat[HouseAddress] {
    def write(h: HouseAddress) =
      JsObject(
        "lat" -> JsNumber(h.coordinates.lat),
        "long" -> JsNumber(h.coordinates.longitude),
        "address" -> JsString(h.address),
        "decision" -> JsBoolean(h.decision.decision),
        "availability" -> JsBoolean(h.availability)
      )
    override def read(json: JsValue): HouseAddress = ???
  }
}

package backend

import app.{Coordinates, Decision, HouseAddress}
import spray.json._

object AddressProtocol extends DefaultJsonProtocol {

  implicit object houseAddressFormat extends RootJsonFormat[HouseAddress] {
    def write(h: HouseAddress) =
      JsObject(
        "lat" -> JsNumber(h.latitude),
        "long" -> JsNumber(h.longitude),
        "address" -> JsString(h.address),
        "decision" -> JsBoolean(h.decision.decision),
        "availability" -> JsBoolean(h.availability),
        "average_price" -> JsNumber(h.price/h.rooms),
        "isWithinPopularArea" -> JsNumber(h.isWithinPopularArea)
      )
    override def read(json: JsValue): HouseAddress = ???
  }
}

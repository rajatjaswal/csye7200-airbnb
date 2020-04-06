package backend

import app.Listing
import spray.json.{DefaultJsonProtocol, JsNumber, JsObject, JsString, JsValue, RootJsonFormat}

object ListingProtocol extends DefaultJsonProtocol{
  implicit object listingFormat extends RootJsonFormat[Listing] {
    def write(l: Listing) =
      JsObject(
        "lat" -> JsNumber(l.latitude),
        "long" -> JsNumber(l.longitude),
        "address" -> JsString(l.address.address),
        "decision" -> JsNumber(l.isWithinPopular)
      )
    override def read(json: JsValue): Listing = ???
  }
}

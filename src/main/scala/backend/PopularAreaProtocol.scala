package backend

import app.PopularArea
import spray.json.{DefaultJsonProtocol, JsNumber, JsObject, JsString, JsValue, RootJsonFormat}

object PopularAreaProtocol extends DefaultJsonProtocol{
  implicit object popularAreaFormat extends RootJsonFormat[PopularArea] {
    def write(p: PopularArea) =
      JsObject(
        "lat" -> JsNumber(p.coordinates.lat),
        "long" -> JsNumber(p.coordinates.longitude),
        "address" -> JsString(p.place),
        "rating" -> JsNumber(p.rating)
      )
    override def read(json: JsValue): PopularArea = ???
  }
}

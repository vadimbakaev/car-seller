import java.time.LocalDate

import play.api.libs.json.{Format, JsResult, JsValue, Json}

package object models {

  implicit val localDateFormat: Format[LocalDate] = new Format[LocalDate] {
    override def reads(json: JsValue): JsResult[LocalDate] = json.validate[String].map(LocalDate.parse)

    override def writes(o: LocalDate): JsValue = Json.toJson(o.toString)
  }

}

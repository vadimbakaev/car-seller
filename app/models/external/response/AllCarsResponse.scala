package models.external.response

import play.api.libs.json.{Format, Json}

import scala.collection.immutable.Seq

final case class AllCarsResponse(
    cars: Seq[CarResponse]
)

object AllCarsResponse {
  implicit val format: Format[AllCarsResponse] = Json.format
}

package models.external.response

import java.time.ZonedDateTime
import java.util.UUID

import models.external.FuelType
import play.api.libs.json.{Format, Json}

final case class CarAdvertResponse(
    id: UUID,
    title: String,
    fuel: FuelType,
    price: Int,
    `new`: Boolean,
    mileage: Option[Int],
    registration: Option[ZonedDateTime]
)

object CarAdvertResponse {
  implicit val format: Format[CarAdvertResponse] = Json.format
}

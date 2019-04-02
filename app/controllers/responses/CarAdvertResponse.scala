package controllers.responses

import java.time.Instant
import java.util.UUID

import controllers.common.FuelType
import play.api.libs.json.{Format, Json}

final case class CarAdvertResponse(
    id: UUID,
    title: String,
    fuel: FuelType,
    price: Int,
    `new`: Boolean,
    mileage: Option[Int],
    `first registration`: Option[Instant]
)

object CarAdvertResponse {
  implicit val format: Format[CarAdvertResponse] = Json.format
}

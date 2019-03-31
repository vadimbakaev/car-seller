package models.response

import java.time.LocalDate
import java.util.UUID

import models.FuelType.FuelType
import play.api.libs.json.{Format, Json}

final case class CarResponse(
    id: UUID,
    title: String,
    fuel: FuelType,
    price: Int,
    `new`: Boolean,
    mileage: Option[Int],
    registration: Option[LocalDate]
)

object CarResponse {
  implicit val format: Format[CarResponse] = Json.format
}

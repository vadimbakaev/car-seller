package mappers

import java.time.LocalDate
import java.util.UUID

import models.{CarInfo, FuelType}
import models.response.CarResponse

class CarInfo2CarResponse extends (CarInfo => CarResponse) {
  override def apply(v1: CarInfo): CarResponse = v1 match {
    case CarInfo(
        id: UUID,
        title: String,
        fuel: String,
        price: Int,
        isNew: Boolean,
        mileage: Option[Int],
        registration: Option[LocalDate]
        ) =>
      CarResponse(id, title, FuelType.withName(fuel), price, isNew, mileage, registration)
  }
}

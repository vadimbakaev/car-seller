package mappers

import java.time.ZonedDateTime
import java.util.UUID

import models.CarInfo
import models.external.FuelType
import models.external.response.CarResponse

class CarInfo2CarResponse extends (CarInfo => CarResponse) {
  override def apply(v1: CarInfo): CarResponse = v1 match {
    case CarInfo(
        id: UUID,
        title: String,
        fuel: String,
        price: Int,
        isNew: Boolean,
        mileage: Option[Int],
        registration: Option[ZonedDateTime]
        ) =>
      CarResponse(id, title, FuelType.values.find(_.toString == fuel).get, price, isNew, mileage, registration)
  }
}

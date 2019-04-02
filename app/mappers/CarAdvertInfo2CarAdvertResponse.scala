package mappers

import java.time.Instant
import java.util.UUID

import controllers.common.FuelType
import controllers.responses.CarAdvertResponse
import services.CarAdvertInfo

class CarAdvertInfo2CarAdvertResponse extends (CarAdvertInfo => CarAdvertResponse) {
  override def apply(v1: CarAdvertInfo): CarAdvertResponse = v1 match {
    case CarAdvertInfo(
        id: UUID,
        title: String,
        fuel: String,
        price: Int,
        isNew: Boolean,
        mileage: Option[Int],
        registration: Option[Instant]
        ) =>
      CarAdvertResponse(id, title, FuelType.values.find(_.toString == fuel).get, price, isNew, mileage, registration)
  }
}

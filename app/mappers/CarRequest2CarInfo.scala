package mappers

import java.time.LocalDate
import java.util.UUID

import com.google.inject.Singleton
import models.CarInfo
import models.FuelType.FuelType
import models.request.CarRequest

@Singleton
class CarRequest2CarInfo extends (CarRequest => CarInfo) {
  override def apply(v1: CarRequest): CarInfo = v1 match {
    case CarRequest(
        id: UUID,
        title: String,
        fuel: FuelType,
        price: Int,
        isNew: Boolean,
        mileage: Option[Int],
        registration: Option[LocalDate]
        ) =>
      CarInfo(
        id,
        title,
        fuel.toString,
        price,
        isNew,
        mileage,
        registration
      )
  }
}

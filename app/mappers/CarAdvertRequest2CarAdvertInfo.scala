package mappers

import java.time.ZonedDateTime
import java.util.UUID

import com.google.inject.Singleton
import models.CarAdvertInfo
import models.external.FuelType
import models.external.request.CarAdvertRequest

@Singleton
class CarAdvertRequest2CarAdvertInfo extends (CarAdvertRequest => CarAdvertInfo) {
  override def apply(v1: CarAdvertRequest): CarAdvertInfo = v1 match {
    case CarAdvertRequest(
        id: UUID,
        title: String,
        fuel: FuelType,
        price: Int,
        isNew: Boolean,
        mileage: Option[Int],
        registration: Option[ZonedDateTime]
        ) =>
      CarAdvertInfo(
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

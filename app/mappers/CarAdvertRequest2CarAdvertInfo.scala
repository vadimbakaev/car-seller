package mappers

import java.time.Instant
import java.util.UUID

import com.google.inject.Singleton
import controllers.common.FuelType
import controllers.requests.CarAdvertRequest
import services.CarAdvertInfo

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
        registration: Option[Instant]
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

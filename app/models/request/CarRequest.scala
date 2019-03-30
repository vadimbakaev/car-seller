package models.request

import java.time.LocalDate
import java.util.UUID

import models.FuelType.FuelType
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads, _}

final case class CarRequest(
    id: UUID,
    title: String,
    fuel: FuelType,
    price: Int,
    `new`: Boolean,
    mileage: Option[Int],
    registration: Option[LocalDate]
)

object CarRequest {

  implicit val writes: Writes[CarRequest] = Json.format
  implicit val reads: Reads[CarRequest] = {
    val newRead: Reads[Boolean] = (__ \ "new").read[Boolean]

    def readConditionally[T](path: JsPath)(implicit r: Reads[T]): Reads[Option[T]] =
      newRead.flatMap { isNew =>
        if (isNew) path.readNullable[T]
        else path.read[T].map(Some(_))
      }

    (
      (__ \ "id").read[UUID] ~
      (__ \ "title").read[String] ~
      (__ \ "fuel").read[FuelType] ~
      (__ \ "price").read[Int] ~
      newRead ~
      readConditionally[Int](__ \ "mileage") ~
      readConditionally[LocalDate](__ \ "registration")
    )(CarRequest.apply _)
  }

}

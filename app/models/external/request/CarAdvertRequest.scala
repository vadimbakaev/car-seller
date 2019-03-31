package models.external.request

import java.time.ZonedDateTime
import java.util.UUID

import models.external.FuelType
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads, _}

final case class CarAdvertRequest(
    id: UUID,
    title: String,
    fuel: FuelType,
    price: Int,
    `new`: Boolean,
    mileage: Option[Int],
    `first registration`: Option[ZonedDateTime]
)

object CarAdvertRequest {

  implicit val writes: Writes[CarAdvertRequest] = Json.format
  implicit val reads: Reads[CarAdvertRequest] = {
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
      readConditionally[ZonedDateTime](__ \ "registration")
    )(CarAdvertRequest.apply _)
  }

}

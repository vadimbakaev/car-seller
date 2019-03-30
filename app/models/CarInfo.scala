package models

import java.time.LocalDate
import java.util.UUID

final case class CarInfo(
    id: UUID,
    title: String,
    fuel: String,
    price: Int,
    isNew: Boolean,
    mileage: Option[Int],
    registration: Option[LocalDate]
)

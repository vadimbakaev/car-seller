package models

import java.time.Instant
import java.util.UUID

final case class CarAdvertInfo(id: UUID,
                               title: String,
                               fuel: String,
                               price: Int,
                               isNew: Boolean,
                               mileage: Option[Int],
                               registration: Option[Instant])

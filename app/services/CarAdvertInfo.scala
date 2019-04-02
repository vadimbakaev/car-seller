package services

import java.time.Instant
import java.util.UUID

import enumeratum._

import scala.collection.immutable

final case class CarAdvertInfo(id: UUID,
                               title: String,
                               fuel: String,
                               price: Int,
                               isNew: Boolean,
                               mileage: Option[Int],
                               registration: Option[Instant])

sealed trait SortKey extends EnumEntry {
  val internalValue: String
}

object SortKey extends Enum[SortKey] {
  val values: immutable.IndexedSeq[SortKey] = findValues

  case object Id extends SortKey {
    override val internalValue: String = "id"
  }
  case object Title extends SortKey {
    override val internalValue: String = "title"
  }
  case object Fuel extends SortKey {
    override val internalValue: String = "fuel"
  }
  case object Price extends SortKey {
    override val internalValue: String = "price"
  }
  case object New extends SortKey {
    override val internalValue: String = "isNew"
  }
  case object Mileage extends SortKey {
    override val internalValue: String = "mileage"
  }
  case object Registration extends SortKey {
    override val internalValue: String = "registration"
  }
}

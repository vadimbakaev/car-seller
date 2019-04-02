package controllers.requests

import java.time.Instant
import java.util.UUID

import controllers.common.FuelType
import controllers.requests.CarAdvertRequestTest._
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json

class CarAdvertRequestTest extends PlaySpec {

  "CarAdvertRequest" should {

    "fail to deserialize from invalid json, missing field for old car advert" in {
      Json
        .obj(
          "id"    -> Id,
          "title" -> "Audi A4 Avant",
          "fuel"  -> "D",
          "price" -> 7000,
          "new"   -> false
        )
        .validate[CarAdvertRequest]
        .isError mustBe true
    }

    "ignore addition field for new car advert" in {
      Json
        .obj(
          "id"                 -> Id,
          "title"              -> "Audi A4 Avant",
          "fuel"               -> "D",
          "price"              -> 7000,
          "new"                -> true,
          "mileage"            -> 99000,
          "first registration" -> "2017-07-10T05:33:44.914Z"
        )
        .validate[CarAdvertRequest]
        .asOpt mustBe Some(
        CarAdvertRequest(
          Id,
          "Audi A4 Avant",
          FuelType.Diesel,
          7000,
          `new` = true,
          None,
          None
        )
      )
    }

    "map addition field for old car advert correctly" in {
      Json
        .obj(
          "id"                 -> Id,
          "title"              -> "Audi A4 Avant",
          "fuel"               -> "D",
          "price"              -> 7000,
          "new"                -> false,
          "mileage"            -> 99000,
          "first registration" -> "2017-07-10T05:33:44.914Z"
        )
        .validate[CarAdvertRequest]
        .asOpt mustBe Some(
        CarAdvertRequest(
          Id,
          "Audi A4 Avant",
          FuelType.Diesel,
          7000,
          `new` = false,
          Some(99000),
          Some(Instant.parse("2017-07-10T05:33:44.914Z"))
        )
      )
    }
  }
}

object CarAdvertRequestTest {
  val Id: UUID = UUID.randomUUID()
}

package models.external.request

import java.util.UUID

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json
import CarAdvertRequestTest._
import models.external.FuelType

class CarAdvertRequestTest extends PlaySpec {

  "CarAdvertRequest" should {

    "fail to deserialize from invalid json" in {
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

    "ingnore addition field for new car advert" in {
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
  }
}

object CarAdvertRequestTest {
  val Id: UUID = UUID.randomUUID()
}

package mappers

import java.time.ZonedDateTime
import java.util.UUID

import models.CarInfo
import models.external.FuelType
import models.external.response.CarResponse
import org.scalatestplus.play.PlaySpec

class CarInfo2CarResponseTest extends PlaySpec {

  "CarInfo2CarResponse" should {
    "correctly map carInfo to response" in {
      val id            = UUID.randomUUID()
      val zonedDateTime = ZonedDateTime.now()
      val carInfo: CarInfo = CarInfo(
        id,
        "title",
        "Diesel",
        9000,
        isNew = false,
        Some(9),
        Some(zonedDateTime)
      )
      val expectedResponse: CarResponse = CarResponse(
        id,
        "title",
        FuelType.Diesel,
        9000,
        `new` = false,
        Some(9),
        Some(zonedDateTime)
      )

      new CarInfo2CarResponse()(carInfo) mustBe expectedResponse
    }
  }
}

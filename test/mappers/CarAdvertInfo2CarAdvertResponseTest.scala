package mappers

import java.time.ZonedDateTime
import java.util.UUID

import models.CarAdvertInfo
import models.external.FuelType
import models.external.response.CarAdvertResponse
import org.scalatestplus.play.PlaySpec

class CarAdvertInfo2CarAdvertResponseTest extends PlaySpec {

  "CarAdvertInfo2CarAdvertResponse" should {
    "correctly map carInfo to response" in {
      val id            = UUID.randomUUID()
      val zonedDateTime = ZonedDateTime.now()
      val carInfo: CarAdvertInfo = CarAdvertInfo(
        id,
        "title",
        "Diesel",
        9000,
        isNew = false,
        Some(9),
        Some(zonedDateTime)
      )
      val expectedResponse: CarAdvertResponse = CarAdvertResponse(
        id,
        "title",
        FuelType.Diesel,
        9000,
        `new` = false,
        Some(9),
        Some(zonedDateTime)
      )

      new CarAdvertInfo2CarAdvertResponse()(carInfo) mustBe expectedResponse
    }
  }
}

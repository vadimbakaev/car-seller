package mappers

import java.time.ZonedDateTime
import java.util.UUID

import controllers.common.FuelType
import controllers.responses.CarAdvertResponse
import org.scalatestplus.play.PlaySpec
import services.CarAdvertInfo

class CarAdvertInfo2CarAdvertResponseTest extends PlaySpec {

  "CarAdvertInfo2CarAdvertResponse" should {
    "correctly map carInfo to responses" in {
      val id      = UUID.randomUUID()
      val instant = ZonedDateTime.now().toInstant
      val carInfo: CarAdvertInfo = services.CarAdvertInfo(
        id,
        "title",
        "Diesel",
        9000,
        isNew = false,
        Some(9),
        Some(instant)
      )
      val expectedResponse: CarAdvertResponse = CarAdvertResponse(
        id,
        "title",
        FuelType.Diesel,
        9000,
        `new` = false,
        Some(9),
        Some(instant)
      )

      new CarAdvertInfo2CarAdvertResponse()(carInfo) mustBe expectedResponse
    }
  }
}

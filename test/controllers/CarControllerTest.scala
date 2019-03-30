package controllers

import java.util.UUID

import mappers.CarRequest2CarInfo
import models.FuelType
import models.commands.AddCarResult
import models.request.CarRequest
import org.mockito.{ArgumentMatchersSugar, MockitoSugar}
import org.scalatest.ParallelTestExecution
import org.scalatest.concurrent.ScalaFutures
import play.api.http.ContentTypes
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers.{status, stubControllerComponents, _}
import play.mvc.Http.HeaderNames
import services.CommandHandler

import scala.concurrent.{ExecutionContext, Future}

class CarControllerTest
    extends ControllerBaseSpec
    with ScalaFutures
    with MockitoSugar
    with ArgumentMatchersSugar
    with ParallelTestExecution {

  trait Fixture {
    implicit val ec: ExecutionContext  = inject[ExecutionContext]
    val commandHandler: CommandHandler = mock[CommandHandler]

    val controller = new CarController(stubControllerComponents(), new CarRequest2CarInfo(), commandHandler)
  }

  "CarController POST" should {
    "on add return BadRequest when request is invalid, empty json" in new Fixture {
      val body: JsValue = Json.obj()
      val request: FakeRequest[JsValue] = FakeRequest(POST, "/public/v1/car")
        .withBody(body)
        .withHeaders(HeaderNames.CONTENT_TYPE -> ContentTypes.JSON)
      val addCarResponse: Future[Result] = controller.add.apply(request)

      status(addCarResponse) mustBe BAD_REQUEST
    }

    "on add return BadRequest when request is invalid" in new Fixture {
      val body: JsValue = Json.obj(
        "id"    -> "18f126d7-708b-4640-83f2-7916b9ad0531",
        "title" -> "Audi A4 Avant",
        "fuel"  -> "D",
        "price" -> 7000,
        "new"   -> false
      )
      val request: FakeRequest[JsValue] = FakeRequest(POST, "/public/v1/car")
        .withBody(body)
        .withHeaders(HeaderNames.CONTENT_TYPE -> ContentTypes.JSON)
      val addCarResponse: Future[Result] = controller.add.apply(request)

      status(addCarResponse) mustBe BAD_REQUEST
      contentAsJson(addCarResponse) mustBe Json.obj(
        "obj.registration" -> Json.arr(Json.obj("msg" -> Json.arr("error.path.missing"), "args" -> Json.arr())),
        "obj.mileage"      -> Json.arr(Json.obj("msg" -> Json.arr("error.path.missing"), "args" -> Json.arr()))
      )
    }

    "on add return Created" in new Fixture {

      val id = "d5449989-95b1-4b0b-a94f-eb653d1171d2"
      when(commandHandler.handle(*)).thenReturn(Future.successful(AddCarResult(id)))

      private val body: JsValue = Json.toJson(
        CarRequest(
          UUID.randomUUID(),
          "Audi A4 Avant",
          FuelType.Diesel,
          7000,
          `new` = true,
          None,
          None
        )
      )
      val request: FakeRequest[JsValue] = FakeRequest(POST, "/public/v1/car")
        .withBody(body)
        .withHeaders(HeaderNames.CONTENT_TYPE -> ContentTypes.JSON)
      val addCarResponse: Future[Result] = controller.add.apply(request)

      status(addCarResponse) mustBe CREATED
      header(HeaderNames.LOCATION, addCarResponse) mustBe Some(s"/public/v1/car/$id")
    }
  }
}

package controllers

import mappers.CarRequest2CarInfo
import models.commands.Command
import org.scalatest.concurrent.ScalaFutures
import play.api.http.ContentTypes
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers.{status, stubControllerComponents, _}
import play.mvc.Http.HeaderNames
import services.CommandHandler

import scala.concurrent.{ExecutionContext, Future}

class AddCarControllerTest extends ControllerBaseSpec with ScalaFutures {

  trait Fixture {
    implicit val ec: ExecutionContext = inject[ExecutionContext]

    val commandHandler: CommandHandler = (command: Command) => ???

    val controller = new AddCarController(stubControllerComponents(), new CarRequest2CarInfo(), commandHandler)
  }

  "AddCarController POST" should {
    "return BadRequest when request is invalid, empty json" in new Fixture {
      private val body: JsObject = Json.obj()
      private val request: FakeRequest[JsObject] = FakeRequest(POST, "/public/v1/car")
        .withBody(body)
        .withHeaders(HeaderNames.CONTENT_TYPE -> ContentTypes.JSON)
      val addCarResponse: Future[Result] = controller.add.apply(request)

      status(addCarResponse) mustBe BAD_REQUEST
    }

    "return BadRequest when request is invalid" in new Fixture {
      private val body: JsObject = Json.obj(
        "id"    -> "18f126d7-708b-4640-83f2-7916b9ad0531",
        "title" -> "Audi A4 Avant",
        "fuel"  -> "D",
        "price" -> 7000,
        "new"   -> false
      )
      private val request: FakeRequest[JsObject] = FakeRequest(POST, "/public/v1/car")
        .withBody(body)
        .withHeaders(HeaderNames.CONTENT_TYPE -> ContentTypes.JSON)
      val addCarResponse: Future[Result] = controller.add.apply(request)

      status(addCarResponse) mustBe BAD_REQUEST
      contentAsJson(addCarResponse) mustBe Json.obj(
        "obj.registration" -> Json.arr(Json.obj("msg" -> Json.arr("error.path.missing"), "args" -> Json.arr())),
        "obj.mileage"      -> Json.arr(Json.obj("msg" -> Json.arr("error.path.missing"), "args" -> Json.arr()))
      )
    }
  }
}

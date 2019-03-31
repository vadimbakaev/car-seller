package controllers

import java.time.ZonedDateTime
import java.util.UUID

import controllers.CarControllerTest._
import mappers.{CarInfo2CarResponse, CarRequest2CarInfo}
import models.CarInfo
import models.external.FuelType
import models.external.request.CarRequest
import org.mockito.{ArgumentMatchersSugar, MockitoSugar}
import org.scalatest.ParallelTestExecution
import org.scalatest.concurrent.ScalaFutures
import play.api.http.ContentTypes
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.mvc.{AnyContent, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{status, stubControllerComponents, _}
import play.mvc.Http.HeaderNames
import services.{CarService, SortKey}

import scala.concurrent.{ExecutionContext, Future}

class CarControllerTest
    extends ControllerBaseSpec
    with ScalaFutures
    with MockitoSugar
    with ArgumentMatchersSugar
    with ParallelTestExecution {

  trait Fixture {
    implicit val ec: ExecutionContext = inject[ExecutionContext]
    val carService: CarService        = mock[CarService]

    val internal   = new CarRequest2CarInfo()
    val external   = new CarInfo2CarResponse()
    val controller = new CarController(stubControllerComponents(), internal, external, carService)

    def createJsValueRequest(method: String, body: JsValue): FakeRequest[JsValue] =
      FakeRequest(method, "/public/v1/cars")
        .withBody(body)
        .withHeaders(HeaderNames.CONTENT_TYPE -> ContentTypes.JSON)

    def createAnyContentRequest(method: String, id: String): FakeRequest[AnyContent] =
      FakeRequest(method, s"/public/v1/cars/$id")

    def executePostRequest(body: JsValue): Future[Result] =
      controller.add(createJsValueRequest(POST, body))

    def executeGetRequest(id: String): Future[Result] =
      controller.get(id)(createAnyContentRequest(GET, id))

    def executeDeleteRequest(id: String): Future[Result] =
      controller.delete(id)(createAnyContentRequest(DELETE, id))

    def executePutRequest(body: JsValue): Future[Result] =
      controller.update(createJsValueRequest(PUT, body))
  }

  "CarController" should {
    "on add return Bad Request when request is invalid, empty json" in new Fixture {
      val body: JsValue                  = Json.obj()
      val addCarResponse: Future[Result] = executePostRequest(body)

      status(addCarResponse) mustBe BAD_REQUEST
    }

    "on add return Bad Request when request is invalid" in new Fixture {
      val body: JsValue                  = Json.toJson(InvalidCarRequest)
      val addCarResponse: Future[Result] = executePostRequest(body)

      status(addCarResponse) mustBe BAD_REQUEST
      contentAsJson(addCarResponse) mustBe Json.obj(
        "obj.registration" -> Json.arr(Json.obj("msg" -> Json.arr("error.path.missing"), "args" -> Json.arr())),
        "obj.mileage"      -> Json.arr(Json.obj("msg" -> Json.arr("error.path.missing"), "args" -> Json.arr()))
      )
    }

    "on add return Conflict when car already created" in new Fixture {
      when(carService.create(*)).thenReturn(Future.successful(None))

      val body: JsValue                  = Json.toJson(ValidCarRequest)
      val addCarResponse: Future[Result] = executePostRequest(body)

      status(addCarResponse) mustBe CONFLICT

      verify(carService, times(1)).create(*)
    }

    "on add return Created" in new Fixture {
      when(carService.create(*)).thenReturn(Future.successful(Some(AudiCarInfo)))

      val body: JsValue                  = Json.toJson(ValidCarRequest)
      val addCarResponse: Future[Result] = executePostRequest(body)

      status(addCarResponse) mustBe CREATED
      header(HeaderNames.LOCATION, addCarResponse) mustBe Some(s"/public/v1/cars/$AudiId")

      verify(carService, times(1)).create(*)
    }

    "on get return Not Found when id is invalid" in new Fixture {
      val id = "777"

      val getCarResponse: Future[Result] = executeGetRequest(id)

      status(getCarResponse) mustBe NOT_FOUND
    }

    "on get return Not Found when carService doesn't return car" in new Fixture {
      when(carService.read(*)).thenReturn(Future.successful(None))

      val getCarResponse: Future[Result] = executeGetRequest(AudiId)

      status(getCarResponse) mustBe NOT_FOUND

      verify(carService, times(1)).read(*)
    }

    "on get return Ok with json body" in new Fixture {
      when(carService.read(*)).thenReturn(Future.successful(Some(AudiCarInfo)))

      val getCarResponse: Future[Result] = executeGetRequest(AudiId)

      status(getCarResponse) mustBe OK
      contentAsJson(getCarResponse) mustBe AudiJsonResponse

      verify(carService, times(1)).read(*)
    }

    "on delete return Not Found when id is invalid" in new Fixture {
      val id = "777"

      val deleteCarResponse: Future[Result] = executeDeleteRequest(id)

      status(deleteCarResponse) mustBe NOT_FOUND
    }

    "on delete return Not Found when carService doesn't return car" in new Fixture {
      when(carService.delete(*)).thenReturn(Future.successful(None))

      val deleteCarResponse: Future[Result] = executeDeleteRequest(AudiId)

      status(deleteCarResponse) mustBe NOT_FOUND

      verify(carService, times(1)).delete(*)
    }

    "on delete return Ok with json body" in new Fixture {
      when(carService.delete(*)).thenReturn(Future.successful(Some(OpelCarInfo)))

      val deleteCarResponse: Future[Result] = executeDeleteRequest(OpelId)

      status(deleteCarResponse) mustBe OK
      contentAsJson(deleteCarResponse) mustBe OpelJsonResponse

      verify(carService, times(1)).delete(*)
    }

    "on update return Not Found carService doesn't return car" in new Fixture {
      when(carService.update(*)).thenReturn(Future.successful(None))

      val body: JsValue                     = Json.toJson(ValidCarRequest)
      val updateCarResponse: Future[Result] = executePutRequest(body)

      status(updateCarResponse) mustBe NOT_FOUND

      verify(carService, times(1)).update(*)
    }

    "on update return No Content whe carService returns car" in new Fixture {
      when(carService.update(*)).thenReturn(Future.successful(Some(AudiCarInfo)))

      val body: JsValue                     = Json.toJson(ValidCarRequest)
      val updateCarResponse: Future[Result] = executePutRequest(body)

      status(updateCarResponse) mustBe NO_CONTENT

      verify(carService, times(1)).update(*)
    }

    "on getAll return Bad Request when sortKy is invalid" in new Fixture {
      val key = "wrongKey"
      val getAllResponse: Future[Result] =
        controller.getAll(key, desc = false)(FakeRequest(GET, s"/public/v1/cars?sort=$key"))

      status(getAllResponse) mustBe BAD_REQUEST

      verify(carService, times(0)).readAll(*, *)
    }

    "on getAll return Ok" in new Fixture {
      when(carService.readAll(SortKey.Title, desc = true)).thenReturn(Future.successful(List(AudiCarInfo)))

      val key = "title"
      val getAllResponse: Future[Result] =
        controller.getAll(key, desc = true)(FakeRequest(GET, s"/public/v1/cars?sort=$key&desc=${true}"))

      status(getAllResponse) mustBe OK

      contentAsJson(getAllResponse) mustBe Json.obj("cars" -> Json.arr(AudiJsonResponse))

      verify(carService, times(1)).readAll(*, *)
    }

  }
}

object CarControllerTest {
  val ValidCarRequest: CarRequest =
    CarRequest(UUID.randomUUID(), "Audi A4 Avant", FuelType.Diesel, 7000, `new` = true, None, None)
  val InvalidCarRequest: CarRequest = ValidCarRequest.copy(`new` = false)
  val AudiId: String                = "d5449989-95b1-4b0b-a94f-eb653d1171d2"
  val AudiCarInfo: CarInfo = CarInfo(
    UUID.fromString(AudiId),
    "Audi A4 Avant",
    "Diesel",
    7000,
    isNew = true,
    None,
    None
  )
  val OpelId: String                           = "18f126d7-708b-4640-83f2-7916b9ad0531"
  val RegistrationZonedDateTime: ZonedDateTime = ZonedDateTime.parse("2017-07-10T05:33:44.914Z")
  val OpelCarInfo: CarInfo = CarInfo(
    UUID.fromString(AudiId),
    "Opel Manta",
    "Gasoline",
    5000,
    isNew = false,
    Some(99000),
    Some(RegistrationZonedDateTime)
  )
  val AudiJsonResponse: JsObject = Json.obj(
    "id"    -> AudiCarInfo.id,
    "title" -> AudiCarInfo.title,
    "fuel"  -> FuelType.Diesel.entryName,
    "price" -> AudiCarInfo.price,
    "new"   -> AudiCarInfo.isNew
  )
  val OpelJsonResponse: JsObject = Json.obj(
    "id"           -> OpelCarInfo.id,
    "title"        -> OpelCarInfo.title,
    "fuel"         -> FuelType.Gasoline.entryName,
    "price"        -> OpelCarInfo.price,
    "new"          -> OpelCarInfo.isNew,
    "mileage"      -> 99000,
    "registration" -> "2017-07-10T05:33:44.914Z"
  )

}

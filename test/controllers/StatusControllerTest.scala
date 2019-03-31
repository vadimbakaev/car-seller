package controllers

import java.time.{Clock, Instant, ZoneId}

import models.external.response.StatusResponse
import play.api.http.ContentTypes
import play.api.libs.json.JsSuccess
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers.{status, stubControllerComponents, _}

import scala.concurrent.{ExecutionContext, Future}

class StatusControllerTest extends ControllerBaseSpec {

  trait Fixture {
    val instant: Instant = Instant.now()

    implicit val clock: Clock         = Clock.fixed(instant, ZoneId.of("UTC"))
    implicit val ec: ExecutionContext = inject[ExecutionContext]

    val controller = new StatusController(stubControllerComponents())
  }

  "StatusController GET" should {
    "return status response with application start instance" in new Fixture {
      val statusResponse: Future[Result] = controller.status(FakeRequest(GET, "/status"))

      status(statusResponse) mustBe OK
      contentType(statusResponse) mustBe Some(ContentTypes.JSON)
      contentAsJson(statusResponse).validate[StatusResponse] mustBe JsSuccess(StatusResponse(instant))
    }

    "always return the same status" in new Fixture {
      val statusResponse1: Future[Result] = controller.status(FakeRequest(GET, "/status"))
      val statusResponse2: Future[Result] = controller.status(FakeRequest(GET, "/status"))

      status(statusResponse1) mustBe status(statusResponse2)
      contentType(statusResponse1) mustBe contentType(statusResponse2)
      contentAsJson(statusResponse1) mustBe contentAsJson(statusResponse2)
    }
  }

}

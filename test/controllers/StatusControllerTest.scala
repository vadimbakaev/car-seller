package controllers

import java.time.{Clock, Instant, ZoneId}

import models.response.StatusResponse
import org.scalatest.ParallelTestExecution
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.http.ContentTypes
import play.api.libs.json.JsSuccess
import play.api.test.Helpers.{status, stubControllerComponents, _}
import play.api.test.{FakeRequest, Injecting}

import scala.concurrent.ExecutionContext

class StatusControllerTest extends PlaySpec with GuiceOneAppPerTest with Injecting with ParallelTestExecution {

  "StatusController GET" should {
    "return status response with application start instance" in {
      val instant = Instant.now()

      implicit val clock: Clock         = Clock.fixed(instant, ZoneId.of("UTC"))
      implicit val ec: ExecutionContext = inject[ExecutionContext]

      val controller     = new StatusController(stubControllerComponents())
      val statusResponse = controller.status.apply(FakeRequest(GET, "/status"))

      status(statusResponse) mustBe OK
      contentType(statusResponse) mustBe Some(ContentTypes.JSON)
      contentAsJson(statusResponse).validate[StatusResponse] mustBe JsSuccess(StatusResponse(instant))
    }

    "always return the same status" in {
      val instant = Instant.now()

      implicit val clock: Clock         = Clock.fixed(instant, ZoneId.of("UTC"))
      implicit val ec: ExecutionContext = inject[ExecutionContext]

      val controller      = new StatusController(stubControllerComponents())
      val statusResponse1 = controller.status.apply(FakeRequest(GET, "/status"))
      val statusResponse2 = controller.status.apply(FakeRequest(GET, "/status"))

      status(statusResponse1) mustBe status(statusResponse2)
      contentType(statusResponse1) mustBe contentType(statusResponse2)
      contentAsJson(statusResponse1) mustBe contentAsJson(statusResponse2)
    }

  }
}

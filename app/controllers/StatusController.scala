package controllers

import java.time.{Clock, Instant}

import javax.inject._
import models.external.response.StatusResponse
import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class StatusController @Inject()(cc: ControllerComponents)(implicit exec: ExecutionContext,
                                                           @Named("clock") clock: Clock)
    extends AbstractController(cc)
    with Logging {

  private val statusResponse: StatusResponse = StatusResponse(Instant.now(clock))

  def status: Action[AnyContent] = Action.async {
    Future(Ok(Json.toJson(statusResponse)))
  }

}

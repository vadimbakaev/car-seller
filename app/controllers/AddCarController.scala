package controllers

import javax.inject._
import models.request.CarRequest
import play.api.Logging
import play.api.libs.json.{JsError, JsValue, Json}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AddCarController @Inject()(cc: ControllerComponents)(implicit exec: ExecutionContext)
    extends AbstractController(cc)
    with Logging {

  def add: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body
      .validate[CarRequest]
      .fold(
        invalid => Future(BadRequest(JsError.toJson(invalid))),
        valid => Future(Ok(Json.obj("status" -> "ok")))
      )

  }

}

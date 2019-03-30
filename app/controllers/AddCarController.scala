package controllers

import javax.inject._
import mappers.CarRequest2CarInfo
import models.commands.{AddCarCommand, ErrorResult, FailedResult, SuccessResult}
import models.request.CarRequest
import play.api.Logging
import play.api.libs.json.{JsError, JsValue, Json}
import play.api.mvc._
import services.CommandHandler

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AddCarController @Inject()(
    cc: ControllerComponents,
    mapper: CarRequest2CarInfo,
    commandHandler: CommandHandler
)(implicit exec: ExecutionContext)
    extends AbstractController(cc)
    with Logging {

  def add: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body
      .validate[CarRequest]
      .fold(
        invalid => Future(BadRequest(JsError.toJson(invalid))),
        valid =>
          commandHandler.handle(AddCarCommand(mapper(valid))).map {
            case SuccessResult => Created(Json.obj("status"             -> "ok"))
            case FailedResult  => Conflict(Json.obj("status"            -> "ko"))
            case ErrorResult   => InternalServerError(Json.obj("status" -> "ko"))
        }
      )
  }

}

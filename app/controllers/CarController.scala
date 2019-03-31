package controllers

import javax.inject._
import mappers.CarRequest2CarInfo
import models.commands.{AddCarCommand, AddCarResult, FailedResult}
import models.request.CarRequest
import play.api.Logging
import play.api.libs.json.{JsError, JsValue}
import play.api.mvc._
import play.mvc.Http.HeaderNames
import services.CommandHandler

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CarController @Inject()(
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
            case AddCarResult(id) => Created.withHeaders(HeaderNames.LOCATION -> s"/public/v1/cars/$id")
            case FailedResult     => Conflict
        }
      )
  }

}

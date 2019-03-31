package controllers

import java.util.UUID

import javax.inject._
import mappers.{CarInfo2CarResponse, CarRequest2CarInfo}
import models.commands._
import models.request.CarRequest
import play.api.Logging
import play.api.libs.json.{JsError, JsValue, Json}
import play.api.mvc._
import play.mvc.Http.HeaderNames
import services.CommandHandler

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

@Singleton
class CarController @Inject()(
    cc: ControllerComponents,
    mapperInternal: CarRequest2CarInfo,
    mapperExternal: CarInfo2CarResponse,
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
          commandHandler.handle(AddCarCommand(mapperInternal(valid))).map {
            case AddCarResult(id) => Created.withHeaders(HeaderNames.LOCATION -> s"/public/v1/cars/$id")
            case FailedResult     => Conflict
            case _                => InternalServerError
        }
      )
  }

  def get(id: String): Action[AnyContent] = Action.async { implicit request =>
    Try(UUID.fromString(id))
      .fold(
        _ => Future.successful(NotFound),
        uuid =>
          commandHandler.handle(GetCarCommand(uuid)).map {
            case CarResult(car) => Ok(Json.toJson(mapperExternal(car)))
            case FailedResult   => NotFound
            case _              => InternalServerError
        }
      )
  }

}

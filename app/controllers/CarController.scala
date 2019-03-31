package controllers

import java.util.UUID

import javax.inject._
import mappers.{CarInfo2CarResponse, CarRequest2CarInfo}
import models.commands._
import models.external.request.CarRequest
import models.external.response.AllCarsResponse
import play.api.Logging
import play.api.libs.json.{JsError, JsValue, Json}
import play.api.mvc._
import play.mvc.Http.HeaderNames
import services.{CommandHandler, SortKey}

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
        invalid => Future.successful(BadRequest(JsError.toJson(invalid))),
        valid =>
          commandHandler.handle(CreateCarCommand(mapperInternal(valid))).map {
            case CarResult(Some(car)) => Created.withHeaders(HeaderNames.LOCATION -> s"/public/v1/cars/${car.id}")
            case _                    => Conflict
        }
      )
  }

  def get(id: String): Action[AnyContent] = Action.async {
    Try(UUID.fromString(id))
      .fold(
        _ => Future.successful(NotFound),
        uuid =>
          commandHandler.handle(ReadCarCommand(uuid)).map {
            case CarResult(Some(car)) => Ok(Json.toJson(mapperExternal(car)))
            case _                    => NotFound
        }
      )
  }

  def delete(id: String): Action[AnyContent] = Action.async {
    Try(UUID.fromString(id))
      .fold(
        _ => Future.successful(NotFound),
        uuid =>
          commandHandler.handle(DeleteCarCommand(uuid)).map {
            case CarResult(Some(car)) => Ok(Json.toJson(mapperExternal(car)))
            case _                    => NotFound
        }
      )
  }

  def update: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body
      .validate[CarRequest]
      .fold(
        invalid => Future.successful(BadRequest(JsError.toJson(invalid))),
        valid =>
          commandHandler.handle(UpdateCarCommand(mapperInternal(valid))).map {
            case CarResult(Some(_)) => NoContent
            case _                  => NotFound
        }
      )
  }

  def getAll(sort: String, desc: Boolean): Action[AnyContent] =
    Action.async {
      SortKey.withNameInsensitiveOption(sort) match {
        case Some(sortKey) =>
          commandHandler.handle(ReadAllCommand(sortKey, desc)).map {
            case AllCarResult(cars) => Ok(Json.toJson(AllCarsResponse(cars.map(mapperExternal))))
            case _                  => NotFound
          }
        case None => Future.successful(BadRequest)
      }
    }
}

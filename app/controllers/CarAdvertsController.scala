package controllers

import java.util.UUID

import javax.inject._
import mappers.{CarAdvertInfo2CarAdvertResponse, CarAdvertRequest2CarAdvertInfo}
import models.external.request.CarAdvertRequest
import models.external.response.AllCarAdvertsResponse
import play.api.Logging
import play.api.libs.json.{JsError, JsValue, Json}
import play.api.mvc._
import play.mvc.Http.HeaderNames
import services.{CarAdvertsService, SortKey}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

@Singleton
class CarAdvertsController @Inject()(
    cc: ControllerComponents,
    mapperInternal: CarAdvertRequest2CarAdvertInfo,
    mapperExternal: CarAdvertInfo2CarAdvertResponse,
    catService: CarAdvertsService
)(implicit exec: ExecutionContext)
    extends AbstractController(cc)
    with Logging {

  def add: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body
      .validate[CarAdvertRequest]
      .fold(
        invalid => Future.successful(BadRequest(JsError.toJson(invalid))),
        valid =>
          catService.create(mapperInternal(valid)).map {
            case Some(advert) => Created.withHeaders(HeaderNames.LOCATION -> s"/public/v1/adverts/${advert.id}")
            case _            => Conflict
        }
      )
  }

  def get(id: String): Action[AnyContent] = Action.async {
    Try(UUID.fromString(id))
      .fold(
        _ => Future.successful(NotFound),
        uuid =>
          catService.read(uuid).map {
            case Some(advert) => Ok(Json.toJson(mapperExternal(advert)))
            case _            => NotFound
        }
      )
  }

  def delete(id: String): Action[AnyContent] = Action.async {
    Try(UUID.fromString(id))
      .fold(
        _ => Future.successful(NotFound),
        uuid =>
          catService.delete(uuid).map {
            case Some(advert) => Ok(Json.toJson(mapperExternal(advert)))
            case _            => NotFound
        }
      )
  }

  def update: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body
      .validate[CarAdvertRequest]
      .fold(
        invalid => Future.successful(BadRequest(JsError.toJson(invalid))),
        valid =>
          catService.update(mapperInternal(valid)).map {
            case Some(_) => NoContent
            case _       => NotFound
        }
      )
  }

  def getAll(sort: String, desc: Boolean): Action[AnyContent] =
    Action.async {
      SortKey.withNameInsensitiveOption(sort) match {
        case Some(sortKey) =>
          catService.readAll(sortKey, desc).map { adverts =>
            Ok(Json.toJson(AllCarAdvertsResponse(adverts.map(mapperExternal))))
          }
        case None => Future.successful(BadRequest)
      }
    }
}

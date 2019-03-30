package common

import java.util.UUID

import play.api.http.HttpErrorHandler
import play.api.mvc._
import play.api.mvc.Results._

import scala.concurrent._
import javax.inject.Singleton
import play.api.Logging
import play.api.libs.json.Json

@Singleton
class ErrorHandler extends HttpErrorHandler with Logging {

  def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] =
    Future.successful(
      Status(statusCode)("A client error occurred: " + message)
    )

  def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    val logMarker: UUID = UUID.randomUUID()
    logger.error(s"[$logMarker] Server error", exception)
    Future.successful(
      InternalServerError(
        Json.obj(
          "status"    -> "ko",
          "logMarker" -> logMarker.toString
        )
      )
    )
  }

}

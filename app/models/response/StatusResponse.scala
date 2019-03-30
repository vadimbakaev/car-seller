package models.response

import java.time.Instant

import play.api.libs.json.{Json, OFormat}

final case class StatusResponse(
    start: Instant
)

object StatusResponse {
  implicit val format: OFormat[StatusResponse] = Json.format
}

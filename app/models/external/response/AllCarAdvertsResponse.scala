package models.external.response

import play.api.libs.json.{Format, Json}

import scala.collection.immutable.Seq

final case class AllCarAdvertsResponse(
    adverts: Seq[CarAdvertResponse]
)

object AllCarAdvertsResponse {
  implicit val format: Format[AllCarAdvertsResponse] = Json.format
}

package services

import com.google.inject.ImplementedBy
import models.CarAdvertInfo
import services.mongo.MongoCarAdvertInfoRepository

import scala.concurrent.Future

@ImplementedBy(classOf[MongoCarAdvertInfoRepository])
trait CarAdvertsInfoRepository extends Repository[Future, CarAdvertInfo, SortKey]

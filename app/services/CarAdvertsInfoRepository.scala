package services

import com.google.inject.ImplementedBy
import models.CarAdvertInfo
import services.mongo.MongoCarAdvertsInfoRepository

import scala.concurrent.Future

@ImplementedBy(classOf[MongoCarAdvertsInfoRepository])
trait CarAdvertsInfoRepository extends Repository[Future, CarAdvertInfo, SortKey]

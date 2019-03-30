package services

import com.google.inject.ImplementedBy
import models.CarInfo
import services.mongo.MongoCarInfoRepository

import scala.concurrent.Future

@ImplementedBy(classOf[MongoCarInfoRepository])
trait CarInfoRepository extends Repository[Future, CarInfo]

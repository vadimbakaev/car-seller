package services.repositories

import com.google.inject.ImplementedBy
import services.repositories.mongo.MongoCarAdvertInfoRepository
import services.{CarAdvertInfo, SortKey}

import scala.concurrent.Future

@ImplementedBy(classOf[MongoCarAdvertInfoRepository])
trait CarAdvertsInfoRepository extends Repository[Future, CarAdvertInfo, SortKey]

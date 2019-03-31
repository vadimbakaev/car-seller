package services

import java.util.UUID

import com.google.inject.{ImplementedBy, Inject, Singleton}
import models.CarInfo
import play.api.Logging

import scala.collection.immutable
import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[CarServiceImpl])
trait CarService {

  def create(car: CarInfo): Future[Option[CarInfo]]
  def read(uuid: UUID): Future[Option[CarInfo]]
  def update(car: CarInfo): Future[Option[CarInfo]]
  def delete(id: UUID): Future[Option[CarInfo]]
  def readAll(criteria: SortKey, desc: Boolean): Future[immutable.Seq[CarInfo]]

}

@Singleton
class CarServiceImpl @Inject()(
    repository: CarInfoRepository
)(implicit ex: ExecutionContext)
    extends CarService
    with Logging {

  override def create(car: CarInfo): Future[Option[CarInfo]] = repository.create(car)

  override def read(id: UUID): Future[Option[CarInfo]] = repository.getById(id)

  override def update(car: CarInfo): Future[Option[CarInfo]] = repository.update(car)

  override def delete(id: UUID): Future[Option[CarInfo]] = repository.deleteById(id)

  override def readAll(criteria: SortKey, desc: Boolean): Future[immutable.Seq[CarInfo]] =
    repository.getAll(criteria, desc)
}

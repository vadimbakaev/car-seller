package services

import java.util.UUID

import com.google.inject.{ImplementedBy, Inject, Singleton}
import play.api.Logging
import services.repositories.CarAdvertsInfoRepository

import scala.collection.immutable.Seq
import scala.concurrent.Future

@ImplementedBy(classOf[CarAdvertsServiceImpl])
trait CarAdvertsService {

  def create(carAdverts: CarAdvertInfo): Future[Option[CarAdvertInfo]]
  def read(uuid: UUID): Future[Option[CarAdvertInfo]]
  def update(carAdverts: CarAdvertInfo): Future[Option[CarAdvertInfo]]
  def delete(id: UUID): Future[Option[CarAdvertInfo]]
  def readAll(criteria: SortKey, desc: Boolean): Future[Seq[CarAdvertInfo]]

}

@Singleton
class CarAdvertsServiceImpl @Inject()(
    repository: CarAdvertsInfoRepository
) extends CarAdvertsService
    with Logging {

  override def create(carAdverts: CarAdvertInfo): Future[Option[CarAdvertInfo]] = repository.create(carAdverts)

  override def read(id: UUID): Future[Option[CarAdvertInfo]] = repository.getById(id)

  override def update(carAdverts: CarAdvertInfo): Future[Option[CarAdvertInfo]] = repository.update(carAdverts)

  override def delete(id: UUID): Future[Option[CarAdvertInfo]] = repository.deleteById(id)

  override def readAll(criteria: SortKey, desc: Boolean): Future[Seq[CarAdvertInfo]] = repository.getAll(criteria, desc)
}

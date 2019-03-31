package services

import java.util.UUID

import scala.collection.immutable.Seq
import scala.language.higherKinds

trait Repository[F[_], T, C] {

  def create(model: T): F[Option[T]]

  def getById(uuid: UUID): F[Option[T]]

  def update(model: T): F[Option[T]]

  def deleteById(id: UUID): F[Option[T]]

  def getAll(criteria: C, desc: Boolean): F[Seq[T]]

}

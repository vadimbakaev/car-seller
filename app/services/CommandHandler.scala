package services

import com.google.inject.{ImplementedBy, Inject, Singleton}
import models.commands._
import play.api.Logging

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[CommandHandlerImpl])
trait CommandHandler {

  def handle(command: Command): Future[CommandResult]

}

@Singleton
class CommandHandlerImpl @Inject()(
    repository: CarInfoRepository
)(implicit ex: ExecutionContext)
    extends CommandHandler
    with Logging {
  override def handle(command: Command): Future[CommandResult] = command match {
    case AddCarCommand(car) =>
      repository
        .create(car)
        .map(maybeCartInfo => AddCarResult(maybeCartInfo.map(_.id.toString)))
    case GetCarCommand(id) =>
      repository
        .getById(id)
        .map(CarResult)
    case DeleteCarCommand(id) =>
      repository
        .deleteById(id)
        .map(CarResult)
  }
}

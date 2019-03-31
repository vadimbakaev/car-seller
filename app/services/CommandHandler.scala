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
    case CreateCarCommand(car) => repository.create(car).map(CarResult)
    case UpdateCarCommand(car) => repository.update(car).map(CarResult)
    case ReadCarCommand(id)    => repository.getById(id).map(CarResult)
    case DeleteCarCommand(id)  => repository.deleteById(id).map(CarResult)
  }
}

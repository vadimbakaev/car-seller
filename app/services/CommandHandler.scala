package services

import com.google.inject.{ImplementedBy, Inject, Singleton}
import models.commands.{AddCarCommand, AddCarResult, Command, CommandResult, FailedResult}
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
        .map {
          case Some(carInfo) => AddCarResult(carInfo.id.toString)
          case _             => FailedResult
        }
  }
}

package services

import com.google.inject.{ImplementedBy, Inject, Singleton}
import models.commands.{AddCarCommand, Command, CommandResult, ErrorResult, FailedResult, SuccessResult}
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
          case Some(_) => SuccessResult
          case _       => FailedResult
        }
        .recover {
          case t =>
            logger.error("Unable to create car", t)
            ErrorResult
        }
  }
}

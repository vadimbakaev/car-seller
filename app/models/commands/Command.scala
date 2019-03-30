package models.commands

import models.CarInfo

sealed trait Command

final case class AddCarCommand(car: CarInfo) extends Command

sealed trait CommandResult

case object SuccessResult extends CommandResult

case object FailedResult extends CommandResult

case object ErrorResult extends CommandResult

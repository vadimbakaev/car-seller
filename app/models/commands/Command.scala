package models.commands

import models.CarInfo

sealed trait Command

final case class AddCarCommand(car: CarInfo) extends Command

sealed trait CommandResult

final case class AddCarResult(carId: String) extends CommandResult

case object FailedResult extends CommandResult

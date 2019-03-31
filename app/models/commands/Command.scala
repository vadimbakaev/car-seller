package models.commands

import java.util.UUID

import models.CarInfo

sealed trait Command

final case class AddCarCommand(car: CarInfo) extends Command

final case class GetCarCommand(id: UUID) extends Command

final case class DeleteCarCommand(id: UUID) extends Command

sealed trait CommandResult

final case class AddCarResult(carId: String) extends CommandResult

final case class CarResult(carInfo: CarInfo) extends CommandResult

case object FailedResult extends CommandResult

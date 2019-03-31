package models.commands

import java.util.UUID

import models.CarInfo

sealed trait Command

final case class CreateCarCommand(car: CarInfo) extends Command

final case class ReadCarCommand(id: UUID) extends Command

final case class UpdateCarCommand(car: CarInfo) extends Command

final case class DeleteCarCommand(id: UUID) extends Command

sealed trait CommandResult

final case class CarResult(carInfo: Option[CarInfo]) extends CommandResult

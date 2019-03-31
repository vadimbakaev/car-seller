package models.commands

import java.util.UUID

import models.CarInfo
import services.SortKey

import scala.collection.immutable

sealed trait Command

final case class CreateCarCommand(car: CarInfo) extends Command

final case class ReadCarCommand(id: UUID) extends Command

final case class UpdateCarCommand(car: CarInfo) extends Command

final case class DeleteCarCommand(id: UUID) extends Command

final case class ReadAllCommand(sortBy: SortKey, desc: Boolean) extends Command

sealed trait CommandResult

final case class CarResult(car: Option[CarInfo]) extends CommandResult

final case class AllCarResult(cars: immutable.Seq[CarInfo]) extends CommandResult

package models.external

import play.api.libs.json.{Format, Json}

object FuelType extends Enumeration {
  implicit val format: Format[FuelType] = Json.formatEnum[FuelType.type](FuelType)
  type FuelType = Value

  val Other: FuelType.Value            = Value("O")
  val CNG: FuelType.Value              = Value("C")
  val Diesel: FuelType.Value           = Value("D")
  val Electric: FuelType.Value         = Value("E")
  val ElectricDiesel: FuelType.Value   = Value("3")
  val ElectricGasoline: FuelType.Value = Value("2")
  val Ethanol: FuelType.Value          = Value("M")
  val Gasoline: FuelType.Value         = Value("B")
  val Hydrogene: FuelType.Value        = Value("H")
  val LPG: FuelType.Value              = Value("L")
}

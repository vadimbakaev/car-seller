package models.external

import enumeratum._
import play.api.libs.json._

import scala.collection.{immutable, Seq}

sealed abstract class FuelType(override val entryName: String) extends EnumEntry

object FuelType extends Enum[FuelType] {
  implicit val format: Format[FuelType] = new Format[FuelType] {
    override def writes(o: FuelType): JsValue =
      JsString(o.entryName)

    override def reads(json: JsValue): JsResult[FuelType] = json match {
      case JsString(str) =>
        FuelType.values
          .find(_.entryName == str)
          .map(JsSuccess(_))
          .getOrElse(JsError(Seq(JsPath -> Seq(JsonValidationError("error.expected.validenumvalue")))))
      case _ => JsError(Seq(JsPath -> Seq(JsonValidationError("error.expected.enumstring"))))
    }
  }

  val values: immutable.IndexedSeq[FuelType] = findValues

  case object Other            extends FuelType("O")
  case object CNG              extends FuelType("C")
  case object Diesel           extends FuelType("D")
  case object Electric         extends FuelType("E")
  case object ElectricDiesel   extends FuelType("3")
  case object ElectricGasoline extends FuelType("2")
  case object Ethanol          extends FuelType("M")
  case object Gasoline         extends FuelType("B")
  case object Hydrogene        extends FuelType("H")
  case object LPG              extends FuelType("L")
}

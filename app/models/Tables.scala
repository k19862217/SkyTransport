package models

import play.api.libs.json.{Writes, Reads, Json}
import slick.lifted.TableQuery

object Tables{

  val UserTable = TableQuery[tables.UserTable]
  val TransactionTable = TableQuery[tables.TransactionTable]
  val AddressTable = TableQuery[tables.AddressTable]
  val PackageTable = TableQuery[tables.PackageTable]
  val WarehouseTable = TableQuery[tables.WarehouseTable]
  val MessageTable = TableQuery[tables.MessageTable]
  val PackageOperationTable = TableQuery[tables.PackageOperationTable]

  private val date_pattern_string = util.Formatters.date_pattern_string

  implicit val JodaDateReads = Reads.jodaDateReads(date_pattern_string)
  implicit val JodaDateWrites = Writes.jodaDateWrites(date_pattern_string)
  implicit val AddressFormat = Json.format[AddressInfo]
  implicit val PackageFormat = Json.format[PackageInfo]
  implicit val TransactionFormat = Json.format[Transaction]
  implicit val UserFormat = Json.format[User]
  implicit val WarehouseFormat = Json.format[WarehouseInfo]
  implicit val MessageFormat = Json.format[Message]
  implicit val PackageOperationFormat = Json.format[PackageOperation]

}

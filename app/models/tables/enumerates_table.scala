package models.tables

/**
  * Created by xiaochen.tian on 11/30/15.
  */

import models._
import MyDriver.api._

class WarehouseTable(_tableTag: Tag) extends Table[WarehouseInfo](_tableTag, "warehouse_table") {
  def * = (id, name, country, city, address, phone, postcode) <> (WarehouseInfo.tupled, WarehouseInfo.unapply)
  /** Maps whole row to an option. Useful for outer joins. */
  def ? = (Rep.Some(id), name, country, city, address, phone, postcode).shaped.<>({r=>import r._; _1.map(_=> WarehouseInfo.tupled((_1.get, _2, _3, _4, _5, _6, _7)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

  /** Database column id SqlType(INT), AutoInc, PrimaryKey */
  val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
  /** Database column name SqlType(VARCHAR), Length(45,true), Default(None) */
  val name: Rep[Option[String]] = column[Option[String]]("name", O.Length(45,varying=true), O.Default(None))
  /** Database column country SqlType(VARCHAR), Length(45,true), Default(None) */
  val country: Rep[Option[String]] = column[Option[String]]("country", O.Length(45,varying=true), O.Default(None))
  /** Database column city SqlType(VARCHAR), Length(255,true), Default(None) */
  val city: Rep[Option[String]] = column[Option[String]]("city", O.Length(255,varying=true), O.Default(None))
  /** Database column address SqlType(VARCHAR), Length(2000,true), Default(None) */
  val address: Rep[Option[String]] = column[Option[String]]("address", O.Length(2000,varying=true), O.Default(None))
  /** Database column phone SqlType(VARCHAR), Length(45,true), Default(None) */
  val phone: Rep[Option[String]] = column[Option[String]]("phone", O.Length(45,varying=true), O.Default(None))
  /** Database column postcode SqlType(VARCHAR), Length(45,true), Default(None) */
  val postcode: Rep[Option[String]] = column[Option[String]]("postcode", O.Length(45,varying=true), O.Default(None))
}

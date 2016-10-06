package models.tables

/**
  * Created by xiaochen.tian on 11/30/15.
  */

import models._
import MyDriver.api._
import MyDriver.support._
import org.joda.time.DateTime
import scala.concurrent.ExecutionContext.Implicits.global

class AddressTable(_tableTag: Tag) extends Table[AddressInfo](_tableTag, "address_table") {
  def * = (id, userId, country, city, address, receiver, phone, postcode, visible, updateTime, createTime
    ) <> (AddressInfo.tupled, AddressInfo.unapply)

  val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
  val userId: Rep[Int] = column[Int]("user_id")
  val country: Rep[Option[String]] = column[Option[String]]("country", O.Length(255,varying=true), O.Default(None))
  val city: Rep[Option[String]] = column[Option[String]]("city", O.Length(255,varying=true), O.Default(None))
  val address: Rep[Option[String]] = column[Option[String]]("address", O.Length(2000,varying=true), O.Default(None))
  val receiver: Rep[Option[String]] = column[Option[String]]("receiver", O.Length(255,varying=true), O.Default(None))
  val phone: Rep[Option[String]] = column[Option[String]]("phone", O.Length(45,varying=true), O.Default(None))
  val postcode: Rep[Option[String]] = column[Option[String]]("postcode", O.Length(45,varying=true), O.Default(None))
  val visible: Rep[Option[String]] = column[Option[String]]("visible", O.Length(2,varying=true), O.Default(None))
  val updateTime: Rep[Option[DateTime]] = column[Option[DateTime]]("update_time", O.Length(45,varying=true), O.Default(None))
  val createTime: Rep[Option[DateTime]] = column[Option[DateTime]]("create_time", O.Length(45,varying=true), O.Default(None))

}
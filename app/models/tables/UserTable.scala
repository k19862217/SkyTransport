package models.tables

/**
  * Created by xiaochen.tian on 11/30/15.
  */

import models._
import MyDriver.api._
import MyDriver.support._
import org.joda.time.DateTime

class UserTable(_tableTag: Tag) extends Table[User](_tableTag, "user_table") {
  def * = (id, email, password, name, sex, phone, mobile, qq, idNumber, isSignUp, lastLoginTime, userType, totalSpent, balance, updateTime, createTime
    ) <> (User.tupled, User.unapply)

  val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
  val email: Rep[String] = column[String]("email", O.Length(45,varying=true))
  val password: Rep[String] = column[String]("password", O.Length(45,varying=true))
  val name: Rep[String] = column[String]("name", O.Length(45,varying=true))
  val sex: Rep[String] = column[String]("sex", O.Length(32,varying=true))
  val phone: Rep[Option[String]] = column[Option[String]]("phone", O.Length(16,varying=true), O.Default(None))
  val mobile: Rep[Option[String]] = column[Option[String]]("mobile", O.Length(16,varying=true), O.Default(None))
  val qq: Rep[Option[String]] = column[Option[String]]("qq", O.Length(20,varying=true), O.Default(None))
  val idNumber: Rep[Option[String]] = column[Option[String]]("ID_number", O.Length(32,varying=true), O.Default(None))
  val isSignUp: Rep[Option[String]] = column[Option[String]]("is_sign_up", O.Length(2,varying=true), O.Default(None))
  val lastLoginTime: Rep[Option[DateTime]] = column[Option[DateTime]]("last_login_time", O.Length(45,varying=true), O.Default(None))
  val userType: Rep[Option[String]] = column[Option[String]]("user_type", O.Length(64,varying=true), O.Default(None))
  val totalSpent: Rep[Option[Double]] = column[Option[Double]]("total_spent", O.Default(None))
  val balance: Rep[Option[Double]] = column[Option[Double]]("balance", O.Default(None))
  val updateTime: Rep[Option[DateTime]] = column[Option[DateTime]]("update_time", O.Length(45,varying=true), O.Default(None))
  val createTime: Rep[Option[DateTime]] = column[Option[DateTime]]("create_time", O.Length(45,varying=true), O.Default(None))

  val index1 = index("email_UNIQUE", email, unique=true)
}

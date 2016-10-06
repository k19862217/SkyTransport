package models.tables

/**
 * Created by ming.zhang on 12/4/15.
 */
import models._
import MyDriver.api._
import models.tables.MyDriver.support._
import org.joda.time.DateTime

class MessageTable(_tableTag: Tag) extends Table[Message](_tableTag, "message_table") {
  def * = (id, from, packageId, transactionId, title, question, answer, status, updateTime, createTime
  ) <> (Message.tupled, Message.unapply)

  val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
  val from: Rep[Option[Int]] = column[Option[Int]]("from_user_id", O.Default(None))
  val packageId: Rep[Option[Int]] = column[Option[Int]]("package_id", O.Default(None))
  val transactionId: Rep[Option[Int]] = column[Option[Int]]("transaction_id", O.Default(None))
  val title: Rep[Option[String]] = column[Option[String]]("title", O.Length(2000,varying=true), O.Default(None))
  val question: Rep[Option[String]] = column[Option[String]]("question", O.Length(2000,varying=true), O.Default(None))
  val answer: Rep[Option[String]] = column[Option[String]]("answer", O.Length(2000,varying=true), O.Default(None))
  val status: Rep[Option[Int]] = column[Option[Int]]("status", O.Default(None))
  val updateTime: Rep[Option[DateTime]] = column[Option[DateTime]]("update_time", O.Length(45,varying=true), O.Default(None))
  val createTime: Rep[Option[DateTime]] = column[Option[DateTime]]("create_time", O.Length(45,varying=true), O.Default(None))
}

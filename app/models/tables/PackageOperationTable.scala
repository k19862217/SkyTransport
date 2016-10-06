package models.tables

/**
 * Created by ming.zhang on 12/4/15.
 */
import models._
import models.tables.MyDriver.api._
import models.tables.MyDriver.support._
import org.joda.time.DateTime

class PackageOperationTable(_tableTag: Tag) extends Table[PackageOperation](_tableTag, "operation_table") {
  def * = (id, from_package_id, to_package_id, operation_type, status, operator_id, updateTime, createTime
  ) <> (PackageOperation.tupled, PackageOperation.unapply)

  val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
  val from_package_id: Rep[Int] = column[Int]("from_package_id")
  val to_package_id: Rep[Int] = column[Int]("to_package_id")
  val operation_type: Rep[Int] = column[Int]("operation_type")
  val status: Rep[Int] = column[Int]("status")
  val operator_id: Rep[Option[Int]] = column[Option[Int]]("operator_id", O.Default(None))
  val updateTime: Rep[Option[DateTime]] = column[Option[DateTime]]("update_time", O.Length(45,varying=true), O.Default(None))
  val createTime: Rep[Option[DateTime]] = column[Option[DateTime]]("create_time", O.Length(45,varying=true), O.Default(None))
}

package models.tables

/**
  * Created by xiaochen.tian on 11/30/15.
  */

import models._
import MyDriver.api._
import MyDriver.support._
import org.joda.time.DateTime

class PackageTable(_tableTag: Tag) extends Table[PackageInfo](_tableTag, "package_table") {
  def * = (id, userId, transactionId, warehouseId, goodsName, goodsPrice, goodsCount, orderNumber, forecastTime, description, goodsWeight, arrivedTime, status, operator, updateTime, createTime
    ) <> (PackageInfo.tupled, PackageInfo.unapply)


  val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
  val userId: Rep[Option[Int]] = column[Option[Int]]("user_id", O.Default(None))
  val transactionId: Rep[Option[Int]] = column[Option[Int]]("transaction_id", O.Default(None))
  val warehouseId: Rep[Option[Int]] = column[Option[Int]]("warehouse_id", O.Default(None))
  val goodsName: Rep[Option[String]] = column[Option[String]]("goods_name", O.Length(200,varying=true), O.Default(None))
  val goodsPrice: Rep[Option[Double]] = column[Option[Double]]("goods_price", O.Default(None))
  val goodsCount: Rep[Option[Int]] = column[Option[Int]]("goods_count", O.Default(None))
  val orderNumber: Rep[Option[String]] = column[Option[String]]("order_number", O.Length(45,varying=true), O.Default(None))
  val forecastTime: Rep[Option[DateTime]] = column[Option[DateTime]]("forecast_time", O.Length(45,varying=true), O.Default(None))
  val description: Rep[Option[String]] = column[Option[String]]("description", O.Length(2000,varying=true), O.Default(None))
  val goodsWeight: Rep[Option[Double]] = column[Option[Double]]("goods_weight", O.Default(None))
  val arrivedTime: Rep[Option[DateTime]] = column[Option[DateTime]]("arrived_time", O.Length(45,varying=true), O.Default(None))
  val status: Rep[Option[String]] = column[Option[String]]("status", O.Length(2,varying=true), O.Default(None))
  val operator: Rep[Option[String]] = column[Option[String]]("operator", O.Length(45,varying=true), O.Default(None))
  val updateTime: Rep[Option[DateTime]] = column[Option[DateTime]]("update_time", O.Length(45,varying=true), O.Default(None))
  val createTime: Rep[Option[DateTime]] = column[Option[DateTime]]("create_time", O.Length(45,varying=true), O.Default(None))

}

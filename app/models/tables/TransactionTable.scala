package models.tables

/**
  * Created by xiaochen.tian on 11/30/15.
  */

import models._
import MyDriver.api._
import MyDriver.support._
import org.joda.time.DateTime

class TransactionTable(_tableTag: Tag) extends Table[Transaction](_tableTag, "transaction_table") {
  def * = (id, userId, addressId, needCombine, needDivide, needInsurance, needCheck, needFirm, needFastDelivery, logisticMethod, deliveryCost, insuranceCost, keepCost, operationCost, totalCost, orderNumber,sendTime, status, updateTime, createTime
    ) <>(Transaction.tupled, Transaction.unapply)

  val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
  val userId: Rep[Option[Int]] = column[Option[Int]]("user_id", O.Default(None))
  val addressId: Rep[Option[Int]] = column[Option[Int]]("address_id", O.Default(None))
  val needCombine: Rep[Option[String]] = column[Option[String]]("need_combine", O.Length(2, varying = true), O.Default(None))
  val needDivide: Rep[Option[String]] = column[Option[String]]("need_divide", O.Length(2, varying = true), O.Default(None))
  val needInsurance: Rep[Option[String]] = column[Option[String]]("need_insurance", O.Length(2, varying = true), O.Default(None))
  val needCheck: Rep[Option[String]] = column[Option[String]]("need_check", O.Length(2, varying = true), O.Default(None))
  val needFirm: Rep[Option[String]] = column[Option[String]]("need_firm", O.Length(2, varying = true), O.Default(None))
  val needFastDelivery: Rep[Option[String]] = column[Option[String]]("need_fast_delivery", O.Length(2, varying = true), O.Default(None))
  val logisticMethod: Rep[Option[String]] = column[Option[String]]("logistic_method", O.Length(2, varying = true), O.Default(None))
  val deliveryCost: Rep[Option[Double]] = column[Option[Double]]("delivery_cost", O.Default(None))
  val insuranceCost: Rep[Option[Double]] = column[Option[Double]]("insurance_cost", O.Default(None))
  val keepCost: Rep[Option[Double]] = column[Option[Double]]("keep_cost", O.Default(None))
  val operationCost: Rep[Option[Double]] = column[Option[Double]]("operation_cost", O.Default(None))
  val totalCost: Rep[Option[Double]] = column[Option[Double]]("total_cost", O.Default(None))
  val orderNumber: Rep[Option[String]] = column[Option[String]]("order_number", O.Length(45, varying = true), O.Default(None))
  val sendTime: Rep[Option[DateTime]] = column[Option[DateTime]]("send_time", O.Length(45, varying = true), O.Default(None))
  val status: Rep[Option[Int]] = column[Option[Int]]("status", O.Default(Some(0)))
  val updateTime: Rep[Option[DateTime]] = column[Option[DateTime]]("update_time", O.Length(45, varying = true), O.Default(None))
  val createTime: Rep[Option[DateTime]] = column[Option[DateTime]]("create_time", O.Length(45, varying = true), O.Default(None))
}

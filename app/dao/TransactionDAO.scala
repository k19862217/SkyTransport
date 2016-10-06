package dao

/**
  * Created by xiaochen.tian on 12/3/15.
  */

import backend.DB
import controllers.TransactionSearchForm
import models.Tables._
import models.{AddressInfo, Transaction, PackageInfo, User}
import models.tables.MyDriver.api._
import models.tables.MyDriver.support._
import models.tables.{AddressTable, TransactionTable, PackageTable, UserTable}

import scala.concurrent.ExecutionContext


object TransactionDAO extends BaseDAO{

  type Type_query = Query[(UserTable, TransactionTable, AddressTable), (User, Transaction, AddressInfo), Seq]

  def get_search_condition_opt(rep:Rep[Option[TransactionTable]],form: TransactionSearchForm) = {
    var result:Rep[Option[Boolean]] = Some(true)

    if (form.id.isDefined){
      result = result && ( rep.map(_.id) === form.id.get )
    }

    if (form.orderNumber.isDefined){
      result = result && ( rep.flatMap(_.orderNumber) like s"%${form.orderNumber.get}%" )
    }

    if (form.status.isDefined){
      result = result && ( rep.flatMap(_.status) === form.status.get )
    }

    result
  }

  def get_search_condition(rep:TransactionTable,form: TransactionSearchForm) = {
    var result:Rep[Option[Boolean]] = Some(true)

    if (form.id.isDefined){
      result = result && ( rep.id === form.id.get )
    }

    if (form.orderNumber.isDefined){
      result = result && ( rep.orderNumber like s"%${form.orderNumber.get}%" )
    }

    if (form.status.isDefined){
      result = result && ( rep.status === form.status.get )
    }

    result
  }

  def get_by_id(transaction_id:Int)(implicit context:ExecutionContext) = {
    db.run(TransactionTable.filter(_.id === transaction_id).result) map ( _.headOption )
  }

  def change_status(transaction_id:Int,new_status:Int) = {
    val query = TransactionTable.filter(_.id === transaction_id).map(_.status).update(Some(new_status))
    db.run(query)
  }

  val basic_query:Type_query  = {
    val step1 = UserTable join TransactionTable on {case (user,trans) =>
        user.id === trans.userId
    } join AddressTable on { case ((user,trans),addr) =>
        trans.addressId === addr.id
    } map { case ((user,trans),addr) =>
      (user,trans,addr)
    }
    step1
  }

  def get_result(query:Type_query,row_num:Int = 50) = {
    val q2 = query.sortBy(_._2.updateTime.desc) take row_num

    db.run( q2.result )
  }

}

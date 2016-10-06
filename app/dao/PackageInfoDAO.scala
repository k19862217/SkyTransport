package dao

/**
  * Created by xiaochen.tian on 12/2/15.
  */

import backend.DB
import controllers.PackageSearchForm
import models.Tables._
import models.{Transaction, PackageInfo, User}
import models.tables.MyDriver.api._
import models.tables.MyDriver.support._
import org.joda.time.DateTime
import models.tables.{TransactionTable, PackageTable, UserTable}

import scala.concurrent.ExecutionContext

object PackageInfoDAO extends BaseDAO{



  type Type_query = Query[(Rep[Option[UserTable]], PackageTable, Rep[Option[TransactionTable]]), (Option[User], PackageInfo, Option[Transaction]), Seq]


  def get_search_condition(rep:PackageTable,form: PackageSearchForm) = {
    var result:Rep[Option[Boolean]] = Some(true)

    if (form.id.isDefined){
      result = result && ( rep.id === form.id.get )
    }

    if (form.goodsName.isDefined){
      result = result && ( rep.goodsName like s"%${form.goodsName.get}%" )
    }

    if (form.status.isDefined){
      result = result && ( rep.status === form.status.get )
    }

    if (form.orderNumber.isDefined){
      result = result && ( rep.orderNumber  === form.orderNumber.get )
    }

    result
  }

  val basic_query: Type_query = {
    val step1 = PackageTable joinLeft UserTable on {case (pack,user) =>
      pack.userId === user.id
    } joinLeft TransactionTable on { case ((pack,user),trans) =>
      pack.transactionId === trans.id
    } map { case ((pack,user),trans) => (user,pack,trans) }
    step1
  }

  def get_one_user_package(user_id:Int):Type_query = {
    val step1 = UserTable join PackageTable on { case (user, pack) =>
      pack.userId === user.id && user.id === user_id
    } joinLeft TransactionTable on { case ((user,pack),trans) =>
      pack.transactionId === trans.id
    } map { case ((user,pack),trans) =>
      (Rep.Some(user),pack,trans)
    }

    step1
  }

  def get_by_id(package_id:Int)(implicit context:ExecutionContext) = {
    db.run(PackageTable.filter(_.id === package_id).result) map ( _.headOption )
  }

  def get_by_multiple_id(package_ids:List[Int])(implicit context:ExecutionContext) = {
    db.run(PackageTable.filter(_.id inSet package_ids).result)
  }

  def change_weight(package_id:Int,goodsWeight:Double) = {
    val query =
      PackageTable.filter(_.id === package_id).map(_.goodsWeight).update(Some(goodsWeight))
    db.run(query)
  }

  def change_status(package_id:Int,new_status:String) = {
      val query = if ( new_status == "1" )
        PackageTable.filter(_.id === package_id).map( x => (x.status,x.arrivedTime)).update(
          ( Some(new_status), Some(DateTime.now) )
        )
        else
          PackageTable.filter(_.id === package_id).map(_.status).update(Some(new_status))
      db.run(query)
  }

  def update(packageInfo:PackageInfo) = {
    db.run(PackageTable update packageInfo)
  }

  def get_result(query:Type_query,row_num:Int = 50) = {
    //println(query.result.statements.mkString("\n"))
    val q2 = query.sortBy(_._2.updateTime.desc) take row_num

    db.run( q2.result )

  }

  def insert_return_id(packageInfo: PackageInfo) = {
    val q = (PackageTable returning PackageTable.map(_.id)) += packageInfo

    db.run( q )
  }

}

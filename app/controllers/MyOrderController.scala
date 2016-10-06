package controllers

import models.PackageDetail
import models.Tables._
import models._
import play.api.data.Forms._
import play.api.data._
import models.tables.MyDriver.api._
import models.tables.MyDriver.support._
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MyOrderController extends Controller with Secured with BaseController{
  /**
   * Order class
   */
  case class OrderBeanModel
  (
    id:Option[String],
    address_id:Option[String],
    address:Option[String],
    package_id:Option[String],
    package_id_1:Option[String],
    package_id_2:Option[String],
    goods_name:Option[String],
    order_number:Option[String],
    logistic_method: Option[String],
    need_insurance: Option[String],
    need_fast_delivery: Option[String],
    status: Option[String],
    update_time: Option[String],
    create_time: Option[String]
    )
  /**
   * Order form
   */
  val orderForm = Form {
    mapping(
      "id" -> optional(text),
      "address_id" -> optional(text),
      "address" -> optional(text),
      "package_id" -> optional(text),
      "package_id_1" -> optional(text),
      "package_id_2" -> optional(text),
      "goods_name" -> optional(text),
      "order_number" -> optional(text),
      "logistic_method" -> optional(text),
      "need_insurance" -> optional(text),
      "need_fast_delivery" -> optional(text),
      "status" -> optional(text),
      "update_time" -> optional(text),
      "create_time" -> optional(text)
    )(OrderBeanModel.apply)(OrderBeanModel.unapply)
  }
  case class getInfoClass(jtStartIndex: Int, jtPageSize: Int)
  val getInfoForm = Form {
    mapping(
      "jtStartIndex" -> number,
      "jtPageSize" -> number
    )(getInfoClass.apply)(getInfoClass.unapply)
  }

  case class deleteOrderClass(id: String)
  val deleteOrderForm = Form {
    mapping(
      "id" -> text
    )(deleteOrderClass.apply)(deleteOrderClass.unapply)
  }
  def myOrder() = Action { implicit request =>
    Ok(views.html.userviews.order())
  }

  def myOrderDetail(order_id: Int) = withAuth {username => implicit request =>
    val getTransactionSql = TransactionTable.filter(t => t.id === order_id && t.userId === username.toInt).joinLeft(AddressTable).on(_.addressId === _.id).result
    val getPackagesSql = PackageTable.filter(p => p.transactionId === order_id && p.userId === username.toInt).result
    val detailValue = db.run(for {
      transactionResult <- getTransactionSql
      packageResult <- getPackagesSql
    } yield (packageResult.toList, transactionResult.headOption.map(_._1), transactionResult.headOption.map(_._2).getOrElse(None)))

    detailValue.map { packageDetail =>
      Ok(views.html.userviews.package_detail(PackageDetail(packageDetail._1, packageDetail._2, packageDetail._3)))
    }
  }

  def execute(executeType: String) = withAuth {username => implicit request =>
    executeType match {
      case _ => Future.successful(BadRequest("未定义请求."))
    }
  }
}

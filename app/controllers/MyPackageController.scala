package controllers

import models.Tables._
import models._
import play.api.data.Forms._
import play.api.data._
import play.api.data.format.Formats._
import play.api.i18n.Messages
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import play.api.libs.json.Json

import models.tables.MyDriver.api._
import models.tables.MyDriver.support._
import play.api.mvc._
import util.Constants
import util.Constants._

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global


class MyPackageController extends Controller with BaseController with Secured{
  case class addPackageClass(package_id: Option[Int], warehouse_id: Int, goods_name: String, goods_price: Double, goods_count: Int, order_number: String, forecast_time: Option[String], description: Option[String])
  val addPackageForm = Form {
    mapping(
      "package_id" -> optional(number),
      "warehouse_id" -> number,
      "goods_name" -> text,
      "goods_price" -> of(doubleFormat),
      "goods_count" -> number,
      "order_number" -> text,
      "forecast_time" -> optional(text),
      "description" -> optional(text)
    )(addPackageClass.apply)(addPackageClass.unapply)
  }

  case class updatePackageClass(id: Int, warehouse_id: Int, goods_name: String, goods_price: Double, goods_count: Int, order_number: String, description: Option[String])

  val updatePackageForm = Form {
    mapping(
      "package_id" -> number,
      "warehouse_id" -> number,
      "goods_name" -> text,
      "goods_price" -> of(doubleFormat),
      "goods_count" -> number,
      "order_number" -> text,
      "description" -> optional(text)
    )(updatePackageClass.apply)(updatePackageClass.unapply)
  }

  case class deletePackageClass(id: List[Int])

  val deletePackageForm = Form {
    mapping(
      "packageId" -> list(number)
    )(deletePackageClass.apply)(deletePackageClass.unapply)
  }

  case class getPackageInfoClass(id: Option[Int], packageType: Option[String], jtStartIndex: Option[Int], jtPageSize: Option[Int], keyWord: Option[String])
  val getPackageInfoForm = Form {
    mapping(
      "id" -> optional(number),
      "packageType" -> optional(text),
      "jtStartIndex" -> optional(number),
      "jtPageSize" -> optional(number),
      "keyWord" -> optional(text)
    )(getPackageInfoClass.apply)(getPackageInfoClass.unapply)
  }

  case class GetForecast(package_id: Int)

  def myPackage(package_id: Option[Int]) = withAuth {username => implicit request =>
    Future.successful(Ok(views.html.userviews.package_main(package_id)))
  }

  def myPackageDetail(package_id: Int) = withAuth { userId => implicit request =>
    val getTransactionSql =
          TransactionTable.joinLeft(AddressTable).on(_.addressId === _.id).filter(_._1.id in PackageTable.filter(_.userId === userId.toInt).filter(_.id === package_id).map(_.transactionId)).result
    val getPackagesSql =
          PackageTable.filter(p => p.id === package_id && p.userId === userId.toInt).result
    val detailValue = db.run(for {
      transactionResult <- getTransactionSql
      packageResult <- getPackagesSql
    } yield (packageResult.toList, transactionResult.headOption.map(_._1), transactionResult.headOption.map(_._2).getOrElse(None)))

    detailValue.map { packageDetail =>
      Ok(views.html.userviews.package_detail(PackageDetail(packageDetail._1, packageDetail._2, packageDetail._3)))
    }
  }

  def render(renderPage: String) = withAuth {username => implicit request =>
    renderPage match {
      case "addForecast" =>
        Future.successful(Ok(views.html.userviews.add_forecast()))
      case "modifyForecast" =>
        val sql = PackageTable.filter(_.userId === username.toInt).filter(_.status inSet List("0" , "1"))
        val result = db.run(sql.result)
        result.map {r =>
          println(r.map(e => e.id -> e.goodsName.getOrElse("")).toMap)
          Ok(views.html.userviews.modify_forecast(r.map(e => e.id ->
            ("商品名: " + e.goodsName.getOrElse("") + " - " +
            "商品价格: " + e.goodsPrice.getOrElse("") + " - " +
            "商品数量: " + e.goodsCount.getOrElse(""))).toMap))
        }
      case "divideCombinePackage" =>
        Future.successful(Ok(views.html.userviews.divide_combine_package()))
      case "managePackage" =>
        Future.successful(Ok(views.html.userviews.manage_package()))
    }
  }

  def execute(executeType: String) = withAuth {username => implicit request =>
    executeType match {
     case "getForecast" =>
       val content = request.body.asJson.get
       val f = db.run(PackageTable.filter(_.id === (content \ "package_id").as[Int]).result)

       f.map {r =>
         Ok(Json.obj(
           "id" -> r.headOption.get.id,
           "warehouseId" -> r.headOption.get.warehouseId,
           "goodsName" -> r.headOption.get.goodsName,
           "goodsCount" -> r.headOption.get.goodsCount,
           "goodsPrice" -> r.headOption.get.goodsPrice,
           "orderNumber" -> r.headOption.get.orderNumber
         ))
       }

     case "addForecast" =>
        bindForm(addPackageForm) match {
          case Right(packageInfo) =>
            val f = db.run(PackageTable.filter(_.orderNumber === packageInfo.order_number).result)
            val r = Await.result(f, Duration(Constants.SQL_WAIT_TIME, MILLISECONDS))
            r.nonEmpty match {
              case true =>
                Future.successful(BadRequest(Messages("order.number.exist.exception")))
              case false =>
                db.run(PackageTable += PackageInfo(
                  0, Some(username.toInt), None, Some(packageInfo.warehouse_id), Some(packageInfo.goods_name),
                  Some(packageInfo.goods_price), Some(packageInfo.goods_count), Some(packageInfo.order_number),
                  Some(packageInfo.forecast_time.get), packageInfo.description, None, None, Some("0"), None, None, None))
                Future.successful(Ok(Messages("forecast.order.add.success")))
            }
          case Left(_) =>
            Future.successful(BadRequest(Messages("wrong.form.format.exception")))
        }
      case "updateForecast" =>
        bindForm(updatePackageForm) match {
          case Right(info) =>
            println(info)
            val f = db.run(PackageTable.filter(p=> p.orderNumber =!= info.order_number && p.id =!= info.id).result)
            val r = Await.result(f, Duration(Constants.SQL_WAIT_TIME, MILLISECONDS))
            r.nonEmpty match {
              case false => Future.successful(BadRequest(Messages("order.number.not.exist.exception")))
              case _ =>
                val status = Await.result(db.run(PackageTable.filter(_.id === info.id).map(_.status).result), Duration(Constants.SQL_WAIT_TIME, MILLISECONDS))
                val sql = status.headOption.get match {
                  case Some(IN_WAREHOUSE_WAITING_FOR_FORECAST) =>
                    PackageTable.filter(_.id === info.id)
                      .map(p => (p.warehouseId, p.orderNumber, p.goodsName, p.goodsPrice, p.goodsCount, p.description, p.status))
                      .update((Some(info.warehouse_id), Some(info.order_number), Some(info.goods_name), Some(info.goods_price),
                        Some(info.goods_count), info.description, Some(IN_WAREHOUSE_WAITING_FOR_COMMIT)))
                  case _ =>
                    PackageTable.filter(_.id === info.id)
                      .map(p => (p.warehouseId, p.orderNumber, p.goodsName, p.goodsPrice, p.goodsCount, p.description))
                      .update((Some(info.warehouse_id), Some(info.order_number), Some(info.goods_name), Some(info.goods_price),
                        Some(info.goods_count), info.description))
                }
                db.run(sql)
                Future.successful(Ok(Messages("forecast.order.update.success")))
            }
          case Left(_) =>
            Future.successful(BadRequest(Messages("wrong.form.format.exception")))
        }
      case "get" =>
        bindForm(getPackageInfoForm) match {
          case Right(info) =>

            var tbsql = PackageTable.filter(p => p.userId === username.toInt && p.status =!= "4" && p.status =!= "7")

            info.packageType match {
              case Some(p) => tbsql = tbsql.filter(_.status inSet p.split(",").map(_.trim))
              case _ =>
            }
            info.keyWord match {
              case Some(key) => tbsql = tbsql.filter( x=>
                  (x.id.asColumnOf[String] like "%" + key + "%") ||
                  (x.orderNumber like "%" + key + "%") ||
                  (x.goodsName like "%" + key + "%") ||
                  (x.forecastTime.asColumnOf[String] like "%" + key + "%")
              )
              case _ =>
            }

            val result = db.run(tbsql.result)

            result.map {r =>
              Ok(Json.obj(
                "Result" -> "OK",
                "Records" -> r.slice(info.jtStartIndex.get, info.jtStartIndex.get + info.jtPageSize.get),
                "TotalRecordCount" -> r.size
              ))
            }
          case Left(_) =>
            Future.successful(BadRequest(Messages("wrong.form.format.exception")))
        }
      case "deleteForecast" =>
        bindForm(deletePackageForm) match {
          case Right(info) =>
            val result = db.run(PackageTable.filter(p => p.userId === username.toInt && p.id.inSet(info.id) &&
              p.status === NOT_IN_WAREHOUSE).delete)
            result.map(_ => Ok(Messages("forecast.order.delete.success")))
          case Left(_) =>
            Future.successful(BadRequest(Messages("wrong.form.format.exception")))
        }
      case _ => Future.successful(BadRequest(Messages("undefined.action.exception")))
    }
  }
}

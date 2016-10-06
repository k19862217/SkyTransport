package controllers

import play.api.data.Forms._
import play.api.data._
import play.api.i18n.Messages
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import play.api.libs.json.Json
import play.api.mvc._
import models.Tables._
import models._
import models.tables.MyDriver.api._
import models.tables.MyDriver.support._
import slick.lifted.TableQuery

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class MyAddressController extends Controller with Secured with BaseController{
  case class getAddressClass(jtStartIndex: Int, jtPageSize: Int, jtSorting: Option[String])
  val getAddressForm = Form {
    mapping(
      "jtStartIndex" -> number,
      "jtPageSize" -> number,
      "jtSorting" -> optional(text)
    )(getAddressClass.apply)(getAddressClass.unapply)
  }

  case class addAddressClass(
    id:Option[String],
    country:Option[String],
    city:Option[String],
    address: Option[String],
    receiver: Option[String],
    phone: Option[String],
    postcode: Option[String],
    update_time: Option[String],
    create_time: Option[String]
  )

  val addAddressForm = Form {
    mapping(
      "id" -> optional(text),
      "country" -> optional(text),
      "city" -> optional(text),
      "address" -> optional(text),
      "receiver" -> optional(text),
      "phone" -> optional(text),
      "postcode" -> optional(text),
      "update_time" -> optional(text),
      "create_time" -> optional(text)
    )(addAddressClass.apply)(addAddressClass.unapply)
  }

  case class optionMap(key: String, value: String)

  implicit val format = Json.format[optionMap]

  def myAddress() = withAuth {username => implicit request =>
    Future.successful(Ok(views.html.userviews.address()))
  }

  def render(pageName: String) = withAuth {username => implicit request =>
    pageName match {
      case "add" =>
        Future.successful(Ok(views.html.userviews.add_address()))
      case "manage" =>
        Future.successful(Ok(views.html.userviews.address()))
    }
  }

  def execute(executeType: String) = withAuth {username => implicit request =>
    executeType match {
      case "add" =>
        bindForm(addAddressForm) match {
          case Right(address) =>
            val sql = AddressTable += AddressInfo(-1, username.toInt, address.country,
              address.city, address.address, address.receiver, address.phone, address.postcode, Some("1"))
            val result = db.run(sql)
            result map(_ => Ok(Messages("insert.action.success")))
          case Left(_) =>
            Future.successful(BadRequest(Messages("wrong.form.format.exception")))
        }
      case "get" =>
        bindForm(getAddressForm) match {
          case Right(address) =>
            val sql = AddressTable.filter(_.userId === username.toInt)
            val result = db.run(sql.result)
            result.map{
              case addressList: Seq[AddressInfo] =>
                Ok(Json.obj(
                  "Result" -> "OK",
                  "Records" -> addressList.slice(address.jtStartIndex, address.jtStartIndex + address.jtPageSize),
                  "TotalRecordCount" -> addressList.size
                ))
            }
          case Left(_) =>
            Future.successful(BadRequest(Messages("wrong.form.format.exception")))
        }
      case "getList" =>
        bindForm(getAddressForm) match {
          case Right(address) =>
            val sql = AddressTable.filter(_.userId === username.toInt)
            val result = db.run(sql.result)
            result map {
              case addressList: Seq[AddressInfo] =>
                Ok(Json.obj(
                  "Result" -> "OK",
                  "Records" -> addressList.map { address=>
                    optionMap(
                      address.id.toString,
                      Array(address.receiver.get, address.country.get, address.city.get, address.address.get).mkString(",")
                    )
                  },
                  "TotalRecordCount" -> addressList.size
                ))
            }
          case Left(_) =>
            Future.successful(BadRequest(Messages("wrong.form.format.exception")))
        }
      case "delete" =>
        bindForm[addAddressClass](addAddressForm) match {
          case Right(address) =>
            val sql = AddressTable.filter(a => a.userId === username.toInt && a.id === address.id.get.toInt).delete
            val result = db.run(sql)
            result map(_ => Ok(Json.obj(
              "Result" -> "OK",
              "Message" -> Messages("delete.action.success")
            )))
          case Left(_) =>
            Future.successful(BadRequest(Messages("wrong.form.format.exception")))
        }
      case _ => Future.successful(BadRequest(Messages("undefined.action.exception")))
    }
 }
}
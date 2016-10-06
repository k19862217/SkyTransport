package controllers

import models.Tables._
import models._
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.Messages
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

import models.tables.MyDriver.api._
import play.api.mvc._
import util.{Payment, Constants}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

class MyAccountController extends Controller with Secured with BaseController{
  /**
    * payment class
    */
  case class PaymentBeanModel
  (
    paymentValue: Int
  )
  /**
    * Payment form
    */
  val paymentForm = Form {
    mapping(
      "payment_value" -> number
    )(PaymentBeanModel.apply)(PaymentBeanModel.unapply)
  }
  def myAccount() = withAuth {username => implicit request =>
    val sql = for {
      userSpent <- UserTable joinLeft TransactionTable on (_.id === _.userId) filter (_._1.id === username.toInt)
    } yield userSpent

    db.run(sql.result) map { implicit result =>
      Ok(views.html.userviews.account(AccountDetail(result.head._1, result.flatMap(_._2).toList)))
    }
  }

  def execute(executeType: String) = withAuth { username => implicit request =>
    executeType match {
      case "charge" =>
        bindForm(paymentForm) match {
          case Right(payment) =>
            val paymentInfo  = PaymentInfo (
              "1",
              "http://notify_url_page",
              "http://return_url_page",
              "1234567890",
              "充值",
              "100",
              "没有内容",
              "http://showUrl",
              "",
              "202.168.66.6"
            )
            val returnHtml = Payment.pay(paymentInfo)
            Future.successful(Ok(returnHtml))
          case Left(_) =>
            Future.successful(BadRequest(Messages("wrong.form.format.exception")))
        }
    }
  }
}
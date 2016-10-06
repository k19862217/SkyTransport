package controllers

import dao.{PackageOperationDAO, UserDAO}
import enumerate.OperationType
import org.joda.time.DateTime
import play.api.Play
import play.api.mvc.Controller
import scala.util.Try

import models.Tables._
import models._
import play.api.data.Forms._
import play.api.data._
import play.api.data.format.Formats._
import play.api.i18n.Messages
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

import models.tables.MyDriver.api._
import util.Constants

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._


/**
  * Created by ming.zhang on 2/26/16.
  */

object OperationController {
  /**
    * Get budget case class
    */
  case class ObjectInfo(name: String, count: Int, price: Double)
  case class Objects(objects: List[ObjectInfo])
  case class dividePackageClass(originPackageID: String, targetPackage: List[List[ObjectInfo]])
  case class combinePackageClass(originPackageIDs: List[String])

  val dividePackageTransactionForm = Form {
    mapping(
      "originPackageId" -> text,
      "targetPackage" -> list(
        list(
          mapping(
            "objectName" -> text,
            "objectCount" -> number,
            "objectPrice" -> of(doubleFormat)
          )(ObjectInfo.apply)(ObjectInfo.unapply)
        )
      )
    )(dividePackageClass.apply)(dividePackageClass.unapply)
  }

  val combinePackageTransactionForm = Form {
    mapping(
      "originPackageIDs" -> list(text)
    )(combinePackageClass.apply)(combinePackageClass.unapply)
  }
}


class OperationController extends Controller with BaseController with Secured{

  def myOrder() = withAuth {username => implicit request =>
    Future.successful(Ok(views.html.userviews.order()))
  }

  val COMBINE_COST = Play.application.configuration.getDouble("combine.cost").get
  val DIVIDE_COST = Play.application.configuration.getDouble("divide.cost").get

  def ParseDouble(s: String): Option[Double] = Try { s.toDouble }.toOption

  def execute(executeType: String) = withAuth {username => implicit request =>
    executeType match {
      case "divide" =>
        bindForm(OperationController.dividePackageTransactionForm) match {
          case Right(info) =>
            UserDAO.get_balance(username.toInt) map { case Some(balance) =>
                if (balance.get < DIVIDE_COST) {
                  BadRequest(s"{'ErrorMessage':'${Messages("no.enough.balance.exception")}'}")
                } else {
                  UserDAO.update_balance(username.toInt, balance.get - DIVIDE_COST)
                  PackageOperationDAO.divide_operation(info.originPackageID.toInt, username.toInt, info)
                  Ok(Messages("transaction.add.success"))
                }
            }
          case Left(_) =>
            Future.successful(BadRequest(Messages("wrong.form.format.exception")))
        }
      case "combine" =>
        bindForm(OperationController.combinePackageTransactionForm) match {
          case Right(info) =>
            UserDAO.get_balance(username.toInt) map { case Some(balance) =>
              if (balance.get < COMBINE_COST) {
                BadRequest(s"{'ErrorMessage':'${Messages("no.enough.balance.exception")}'}")
              } else {
                PackageOperationDAO.combine_operation(info, username.toInt)
                UserDAO.update_balance(username.toInt, balance.get - COMBINE_COST)
                Ok(Messages("transaction.add.success"))
              }
            }
          case Left(_) =>
            Future.successful(BadRequest(Messages("wrong.form.format.exception")))
        }
      case _ => Future.successful(BadRequest(withNotDefineAction()))
    }
  }
}



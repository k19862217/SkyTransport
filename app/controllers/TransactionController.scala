package controllers

import models.Tables._
import org.joda.time.{DateTime, Duration}
import org.joda.time.format.DateTimeFormat
import play.api.Play
import play.api.data.Forms._
import play.api.data._
import play.api.data.format.Formats._
import play.api.i18n.Messages
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import play.api.libs.json.Json
import play.api.mvc._
import models.tables.MyDriver.api._
import models._
import util.Constants

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try

class TransactionController extends Controller with BaseController with Secured{

  /**
    * Get budget case class
    */
  case class Packages(id: Int, goodsName: String, goodsPrice: Double, goodsWeight: Option[Double], arrivedTime: Option[String])
  case class ObjectInfo(name: String, count: Int, price: Double)
  case class Objects(objects: List[ObjectInfo])
  case class ServiceInfo(combine: Boolean, insurance: Boolean, check: Boolean, firm: Boolean, sendMethod: String)
  case class budgetClass(packageInfo: List[Packages], serviceInfo: ServiceInfo, addressId: Option[String])
  case class dividePackageClass(originPackageID: String, targetPackage: List[Objects])
  case class combinePackageClass(originPackageIDs: List[String], originPackageNames: List[String])
  case class returnCaseClass(deliveryCost: Double, operationCost: Double, keepingCost: Double, insuranceCost: Double, totalCost: Double)
  case class getInfoClass(jtStartIndex: Int, jtPageSize: Int)

  val getInfoForm = Form {
    mapping(
      "jtStartIndex" -> number,
      "jtPageSize" -> number
    )(getInfoClass.apply)(getInfoClass.unapply)
  }
  /**
   * Get budget form
   */
  val budgetForm = Form {
    mapping(
      "packageInfo" -> list(
        mapping(
          "id" -> number,
          "goodsName" -> text,
          "goodsPrice" -> of(doubleFormat),
          "goodsWeight" -> optional(of(doubleFormat)),
          "arrivedTime" -> optional(text)
        )(Packages.apply)(Packages.unapply)
      ),
      "serviceInfo" -> mapping(
        "combine" -> of(booleanFormat),
        "insurance" -> of(booleanFormat),
        "check" -> of(booleanFormat),
        "firm" -> of(booleanFormat),
        "sendMethod" -> text
      )(ServiceInfo.apply)(ServiceInfo.unapply),
      "addressId" -> optional(text)
    )(budgetClass.apply)(budgetClass.unapply)
  }

  val dividePackageTransactionForm = Form {
    mapping(
      "originPackageId" -> text,
      "targetPackage" -> list(
        mapping(
          "object" -> list(
            mapping(
              "objectName" -> text,
              "objectCount" -> number,
              "objectPrice" -> of(doubleFormat)
            )(ObjectInfo.apply)(ObjectInfo.unapply)
          )
        )(Objects.apply)(Objects.unapply)
      )
    )(dividePackageClass.apply)(dividePackageClass.unapply)
  }

  val combinePackageTransactionForm = Form {
    mapping(
      "originPackageIDs" -> list(text),
      "originPackageNames" -> list(text)
    )(combinePackageClass.apply)(combinePackageClass.unapply)
  }

  case class deleteOrderClass(id: String)
  val deleteOrderForm = Form {
    mapping(
      "id" -> text
    )(deleteOrderClass.apply)(deleteOrderClass.unapply)
  }
  def transaction() = withAuth {username => implicit request =>
    Future.successful(Ok(views.html.userviews.submit_order()))
  }

  val CHECK_COST = Play.application.configuration.getDouble("check.cost").get
  val INSURANCE_COST_PERCENT = Play.application.configuration.getDouble("insurance.cost.percent").get
  val FIRM_COST = Play.application.configuration.getDouble("firm.cost").get
  val COMBINE_COST = Play.application.configuration.getDouble("combine.cost").get
  val MAX_FREE_KEEPING_DAY = Play.application.configuration.getInt("max.free.keep.day").get
  val KEEPING_COST_PER_DAY = Play.application.configuration.getDouble("keeping.cost.per.day").get

  def ParseDouble(s: String): Option[Double] = Try { s.toDouble }.toOption
  def GetBudget(info : budgetClass) = {
    var operationCost = 0.0
    var insuranceCost = 0.0
    var deliveryCost = 0.0
    var keepingCost = 0.0

    val priceMap = scala.util.parsing.json.JSON.parseFull(Constants.PRICE_JSON).get.asInstanceOf[Map[String,Map[String, List[Double]]]]
    if (info.serviceInfo.check) operationCost += CHECK_COST
    if (info.serviceInfo.combine) operationCost += COMBINE_COST
    if (info.serviceInfo.firm) operationCost += FIRM_COST
    if (info.serviceInfo.insurance) insuranceCost +=  info.packageInfo.map(_.goodsPrice).sum * INSURANCE_COST_PERCENT
    var lastKey = 0.0
    val dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm")
    for (p <- info.packageInfo) {
      for((k, v) <- priceMap.toSeq.sortBy(_._1.toDouble)) {
        p.goodsWeight match {
          case Some(goodsWeight) =>
            if (goodsWeight <= ParseDouble(k).get && goodsWeight > lastKey) {
              deliveryCost += priceMap.get(k).get(info.serviceInfo.sendMethod).head
              operationCost += priceMap.get(k).get(info.serviceInfo.sendMethod).last
            }
            lastKey = ParseDouble(k).get
          case _ =>
        }
      }
      p.arrivedTime match {
        case Some(arrivedTime) =>
          keepingCost += (new Duration(DateTime.parse(arrivedTime, dateFormat) , DateTime.now).getStandardDays.asInstanceOf[Int] match {
            case x: Int if x > MAX_FREE_KEEPING_DAY => (x - MAX_FREE_KEEPING_DAY) * KEEPING_COST_PER_DAY
            case _ => 0.0
          })
        case _ =>
      }
    }
    implicit val format = Json.format[returnCaseClass]
    Json.obj("Result" -> returnCaseClass(deliveryCost, operationCost, keepingCost, insuranceCost, deliveryCost + operationCost + keepingCost + insuranceCost))
  }

  def render(renderPage: String) = withAuth {username => implicit request =>
    renderPage match {
      case "submitOrder" =>
        Future.successful(Ok(views.html.userviews.submit_order()))
      case _ =>
        Future.successful(BadRequest(Messages("wrong.form.format.exception")))
    }
  }

  def execute(executeType: String) = withAuth {username => implicit request =>
    executeType match {
      case "getBudget" =>
        bindForm(budgetForm) match {
          case Right(budget) =>
            Future.successful(Ok(GetBudget(budget)))
          case Left(_) =>
            Future.successful(BadRequest(Messages("wrong.form.format.exception")))
        }
      case "get" =>
        bindForm(getInfoForm) match {
          case Right(info) =>
            val result = db.run(TransactionTable.filter(_.userId === username.toInt).sortBy(_.id).result)
            result.map {r =>
              Ok(Json.obj(
                "Result" -> "OK",
                "Records" -> r.slice(info.jtStartIndex, info.jtStartIndex + info.jtPageSize),
                "TotalRecordCount" -> r.size
              ))
            }
          case Left(_) =>
            Future.successful(BadRequest(Messages("wrong.form.format.exception")))
        }
      case "create" =>
        bindForm(budgetForm) match {
          case Right(info) =>
            val budget = GetBudget(info)
            db.run(UserTable.filter(_.id === username.toInt).map(_.balance).result) map { balance =>
              if (balance.head.get < (budget \ "Result" \ "totalCost").as[Double]) {
                BadRequest("{ \"ErrorMessage\":\"" + Messages("no.enough.balance.exception") + "\"}")
              } else {
                val updateAccountSql = sqlu"""
                  Update user_table SET balance = CASE
                  WHEN balance > ${(budget \ "Result" \ "totalCost").as[Double]}
                  THEN (balance - ${(budget \ "Result" \ "totalCost").as[Double]})
                  ELSE balance
                  END
                  where id = ${username.toInt}
                """
                val insertTransactionSql = TransactionTable returning TransactionTable.map(_.id) += Transaction(
                  0,
                  Some(username.toInt),
                  Some(info.addressId.get.toInt),
                  if(info.serviceInfo.combine) Some("1") else Some("0"),
                  // TODO Divide
                  Some("0"),
                  if(info.serviceInfo.insurance) Some("1") else Some("0"),
                  if(info.serviceInfo.check) Some("1") else Some("0"),
                  if(info.serviceInfo.firm) Some("1") else Some("0"),
                  // TODO FastDelivery
                  Some("0"),
                  Some(info.serviceInfo.sendMethod),
                  Some((budget \ "Result" \ "deliveryCost").as[Double]),
                  Some((budget \ "Result" \ "insuranceCost").as[Double]),
                  Some((budget \ "Result" \ "keepingCost").as[Double]),
                  Some((budget \ "Result" \ "operationCost").as[Double]),
                  Some((budget \ "Result" \ "totalCost").as[Double]),
                  None,None,
                  // Change Status to Payed
                  Some(Constants.ORDER_PAYED)
                )

                val action =(for {
                  id <- insertTransactionSql
                  _ <- PackageTable.filter(_.id inSet info.packageInfo.map(_.id)).map(t=> (t.transactionId,t.status)).update((Some(id), Some(Constants.COMMIT_WAITING_PROCESSING)))
                  _ <- updateAccountSql
                } yield ()).transactionally
                val result = db.run(action)

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


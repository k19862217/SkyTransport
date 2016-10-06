package controllers.admin

/**
  * Created by tedrahedron on 2/8/16.
  */
import controllers._
import dao._
import models._
import models.tables.{PackageTable, UserTable}
import play.api.libs.json.Json
import play.api.mvc._
import backend.DB
import models.tables.MyDriver.api._
import models.tables.MyDriver.support._
import models.Tables._
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.data._
import play.api.data.Forms._

import scala.concurrent.Future

import play.api.data.format.Formats._
import util.Formatters._


class DetailedController extends Controller {

  val action = (LoginedAction andThen AdminAction)

  def detailed_package(id:Int) = action.async {
    PackageInfoDAO.get_by_id(id) map { pack =>
      Ok( views.html.adminviews.detailed_package(pack.get) )
    }
  }

  def update_package(id:Int) = action.async { implicit request =>
    case class Update_form( arrivedTime:Option[String],
                            goodsWeight:Option[Double],
                            orderNumber:Option[String],
                            status:Option[String],
                            description:Option[String])

    val form = Form(
      mapping(
        "arrivedTime" -> optional(text),
        "goodsWeight" -> optional(of(doubleFormat)),
        "orderNumber" -> optional(text),
        "status" -> optional(text),
        "description" -> optional(text)
      )(Update_form.apply)(Update_form.unapply)
    )

    val update = form.bindFromRequest.get

    PackageInfoDAO.get_by_id(id).flatMap{ case Some(pack) =>
      val new_pack = pack.copy(
        arrivedTime = update.arrivedTime map {util.Formatters.time_pattern.parseDateTime},
        goodsWeight = update.goodsWeight,
        orderNumber = update.orderNumber,
        status = update.status,
        description = update.description)

      if ( new_pack != pack ){
        DB.db.run(PackageTable.insertOrUpdate(new_pack)) map { _ =>
          Ok( views.html.adminviews.detailed_package(new_pack) )
        }

      } else {
        Future{Ok( views.html.adminviews.detailed_package(pack) )}
      }

      //case _ => Future{Ok( views.html.adminviews.detailed_package(null) )}
    }

  }


  def update_transaction(id:Int) = action.async{ implicit request =>

    TransactionDAO.get_by_id(id) flatMap { case Some(t_old) =>

      println(t_old)

      val form = Form(
        mapping(
          "id" -> ignored(id),
          "userId" -> ignored( t_old.userId ),
          "addressId" -> ignored( t_old.addressId ),
          "check_combine" -> optional(of(StringBoolFormatter)),
          "check_divide" -> optional(of(StringBoolFormatter)),
          "check_insurance" -> optional(of(StringBoolFormatter)),
          "check_check" -> optional(of(StringBoolFormatter)),
          "check_firm" -> optional(of(StringBoolFormatter)),
          "check_fastdelivery" -> optional(of(StringBoolFormatter)),
          "logisticMethod" -> optional(text),
          "deliveryCost" -> optional(of(doubleFormat)),
          "insuranceCost" -> optional(of(doubleFormat)),
          "keepCost" -> optional(of(doubleFormat)),
          "operationCost" -> optional(of(doubleFormat)),
          "totalCost" -> optional(of(doubleFormat)),
          "orderNumber" -> optional(text),
          "sendTime" -> ignored( t_old.sendTime ),
          "status" -> optional(number),
          "updateTime" -> ignored( t_old.updateTime ),
          "createTime" -> ignored( t_old.createTime )
        )(Transaction.apply)(Transaction.unapply)
      )

      val t_new = form.bindFromRequest.get

      val is_ship = t_new.status == Some(2) && (t_old.status.isEmpty ||  t_old.status == Some(1))
      val result_page = Redirect( s"/admin/detailed/transaction/$id" )

      DB.db.run(TransactionTable.insertOrUpdate(t_new)) flatMap { _ =>

        if ( is_ship ){
          backend.Logistic.ship(t_new, false) map ( _ => result_page)
        }
        else Future(result_page)
      }
      case _ => null
    }

  }



  def detailed_transaction(id:Int) = action.async {

    for {
      trans <- TransactionDAO.get_by_id(id)
      pack_seq <- DB.db.run(PackageTable.filter(_.transactionId === id).result)
    } yield {
      Ok( views.html.adminviews.detailed_transaction(trans.get, pack_seq ) )
    }

  }

}

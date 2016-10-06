package controllers.admin

/**
  * Created by tedrahedron on 2/13/16.
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

import org.joda.time.DateTime

class DividePackageController extends Controller {

  val action = (LoginedAction andThen AdminAction)

  def insert_num_package(num:Int,id:Int) = action.async { implicit request =>
    val content = request.body.asFormUrlEncoded.get

    PackageInfoDAO.get_by_id(id) flatMap { case Some(pack) =>

      val old_pack = pack.copy(status = Some("4"))
      println("old_pack",old_pack)

      val new_packs = for (i <- 1 to num) yield {
        val suffix = s"_$i"
        val forecastTime = content(s"forecastTime$suffix").headOption map {
          time_pattern.parseDateTime
        }
        val arrivedTime = content(s"arrivedTime$suffix").headOption map {
          time_pattern.parseDateTime
        }

        val form = Form(
          mapping(
            "id" -> ignored(-1),
            "userId" -> ignored( pack.userId ),
            "transactionId" -> ignored( pack.transactionId ),
            "warehouseId" -> optional(number),
            s"goodsName$suffix" -> optional(text),
            s"goodsPrice$suffix" -> optional(of(doubleFormat)),
            s"goodsCount$suffix" -> optional(number),
            s"orderNumber$suffix" -> optional(text),
            s"forecastTime$suffix" -> ignored(forecastTime),
            s"description$suffix" -> optional(text),
            s"goodsWeight$suffix" -> optional(of(doubleFormat)),
            s"arrivedTime$suffix" -> ignored(arrivedTime),
            s"status$suffix" -> optional(text),
            s"operator$suffix" -> optional(text),
            s"updateTime$suffix" -> ignored(None: Option[DateTime]),
            s"createTime$suffix" -> ignored(None: Option[DateTime])
          )(PackageInfo.apply)(PackageInfo.unapply)
        )

        val new_pack = form.bindFromRequest.get

        new_pack
      } // end for

      DB.db.run{DBIO.seq(
        PackageTable.insertOrUpdate(old_pack),
        PackageTable ++= new_packs
        )} map { result =>
        Redirect("/admin/view_package")
      }
    }
  }

  def divide(num:Int,package_id:Int) = action.async {
    PackageInfoDAO.get_by_id(package_id) map { pack =>
      Ok(views.html.adminviews.divide_package(num, pack.get))
    }
  }

}

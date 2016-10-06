package controllers.admin

/**
  * Created by xiaochen.tian on 12/1/15.
  */


import controllers._
import dao._
import models.{PackageInfo, User}
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

import PackageInfoDAO._
class PackageManagerController extends Controller {

  val action = (LoginedAction andThen AdminAction)

  val myview = views.html.adminviews.package_manage

  def default_search() = action.async {
    val query = PackageInfoDAO.basic_query

    PackageInfoDAO.get_result(query) map { x =>
      Ok(myview(x))
    }
  }

  def change_package_status(id: Int, new_status: String) = action.async {

    val f_result = PackageInfoDAO.change_status(id, new_status).flatMap {
      x =>
        if (x>0) {
          get_by_id(id) map { pack =>
            println(Json.obj("result" -> "OK","packageInfo" -> pack.get))
            Ok( Json.obj("result" -> "OK","packageInfo" -> pack.get) )
          }
        }
        else {
          Future {
            Ok( Json.obj("result" -> "ERROR", "msg"-> "No Row Update ERROR")) }
        }
    } recover { case _ => Ok( Json.obj("result" -> "ERROR", "msg"-> "Inner ERROR")) }

    f_result

//    PackageInfoDAO.get_by_id(id) flatMap {
//      case Some(oldpackage) =>
//        if (new_status == "" && oldpackage.status == Some("")){
//        }
//          f_result
//    } recover { case _ => Ok( Json.obj("result" -> "ERROR", "msg"-> "Inner ERROR")) }

  }

  def change_package_weight(id: Int, goodsWeight: Double) = action.async {
    PackageInfoDAO.change_weight(id, goodsWeight).flatMap {
      x =>
        if (x>0) {
          get_by_id(id) map { pack =>
            println(pack)
            Ok( Json.obj("result" -> "OK","packageInfo" -> pack.get) )
          }
        }
        else {
          Future {
            Ok( Json.obj("result" -> "ERROR", "msg"-> "No Row Update ERROR")) }
        }
    } recover { case _ => Ok( Json.obj("result" -> "ERROR", "msg"-> "Inner ERROR")) }

  }

  def search() = Action.async{ implicit request =>
    val pack_form = PackageSearchForm.form.bindFromRequest.get
    val trans_form = TransactionSearchForm.form.bindFromRequest.get

    val query:Type_query = basic_query filter ( x =>
        PackageInfoDAO.get_search_condition(x._2,pack_form) &&
        TransactionDAO.get_search_condition_opt(x._3,trans_form)
      )

    get_result(query) map { x=>
      Ok(myview(x))
    }
  }

}

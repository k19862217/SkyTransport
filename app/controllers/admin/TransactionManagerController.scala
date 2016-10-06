package controllers.admin

import controllers._
import dao.PackageInfoDAO._
import dao.TransactionDAO.Type_query
import dao.TransactionDAO._
import dao.TransactionDAO.basic_query
import dao.TransactionDAO.get_result
import dao._
import models.{PackageInfo, User}
import models.tables.{PackageTable, UserTable,TransactionTable}
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

/**
  * Created by xiaochen.tian on 12/8/15.
  */
class TransactionManagerController extends Controller {
  val action = (LoginedAction andThen AdminAction)

  val myview = views.html.adminviews.transaction_manage

  def change_transaction_status(id: Int, new_status: Int) = action.async {

    for ( t_old <- TransactionDAO.get_by_id(id)) yield {
      t_old.get
    }

    TransactionDAO.change_status(id, new_status).flatMap {
      x =>
        if (x>0) {

          TransactionDAO.get_by_id(id) map { transaction =>

            Ok( Json.obj("result" -> "OK","transaction" -> transaction.get) )
          }
        }
        else {
          Future {
            Ok( Json.obj("result" -> "ERROR", "msg"-> "No Row Update ERROR")) }
        }
    } recover { case _ => Ok( Json.obj("result" -> "ERROR", "msg"-> "Inner ERROR")) }

  }

  def default_search() = action.async {
    val query = basic_query

    get_result(query) map { x =>
      x foreach println
      Ok(myview(x))
    }
  }


  def search() = action.async{ implicit request =>
    val user_form = UserSearchForm.form.bindFromRequest.get
    val trans_form = TransactionSearchForm.form.bindFromRequest.get

    val query:Type_query = basic_query filter ( x =>
      UserDAO.get_search_condition(x._1, user_form ) &&
        TransactionDAO.get_search_condition(x._2,trans_form)
      )

    get_result(query) map { x=>
      Ok(myview(x))
    }
  }

}

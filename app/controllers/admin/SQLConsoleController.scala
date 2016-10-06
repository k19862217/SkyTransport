package controllers.admin

import controllers._
import play.api.mvc._
import backend.DB
import models.tables.MyDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.data._
import play.api.data.Forms._

import scala.concurrent.Future

/**
  * Created by xiaochen.tian on 12/1/15.
  */
class SQLConsoleController extends Controller {


  //def run_sql() = (LoginedAction andThen SuperAdminAction).async { request => request.method match {
  def run_sql() = Action.async { request => request.method match {
    case "GET" =>
      Future {
        Ok(views.html.adminviews.sqlconsole("",Seq.empty[String],Seq.empty[Seq[String]]))
      }

    case "POST" =>
      println("DEBUG",request)
      println("DEBUG",request.body.asText)
      println("DEBUG",request.body.asFormUrlEncoded)

      val form = request.body.asFormUrlEncoded.get
      val sql_str:String = form("sql_txt").head

      val step1 = SimpleDBIO { x=>
          val resultset = x.connection.createStatement().executeQuery(sql_str)
          var content:Seq[Seq[String]] = Vector.empty
          val meta = resultset.getMetaData
          val ncol = meta.getColumnCount
          val headers:Seq[String] = for (
              i <- 1 to ncol
          ) yield meta.getColumnLabel(i)

          while (resultset.next) {
            val row = 1 to ncol map resultset.getString
            content = content :+ row
          }
          (headers,content)
      }

      DB.db.run( step1 ) map {
        case (headers,content) =>
          Ok(views.html.adminviews.sqlconsole(sql_str,headers,content))
      }

    }
  }
}

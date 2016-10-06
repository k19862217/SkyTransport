package controllers

import models.Message
import org.joda.time.DateTime

import scala.concurrent.{Future, Await}
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.Messages
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import play.api.libs.json.Json
import play.api.mvc._
import models.Tables._
import models.tables.MyDriver.api._

/**
  * Created by Akira on 15/12/5.
  */
class MessageController extends Controller with Secured with BaseController{
  def myMessage() = withAuth {username => implicit request =>
    val sql = MessageTable.filter (u=> u.from === username.toInt).sortBy(_.id.desc)

    db.run(sql.result) map { result =>
      Ok(views.html.userviews.message(result, username.toInt))
    }
  }

  case class addMessageClass(
                              packageId:Option[Int],
                              transactionId:Option[Int],
                              title:Option[String],
                              question: Option[String]
                            )
  val addMessageForm = Form {
    mapping(
      "package_id" -> optional(number),
      "transaction_id" -> optional(number),
      "title" -> optional(text),
      "question" -> optional(text)
    )(addMessageClass.apply)(addMessageClass.unapply)
  }
  def execute(executeType: String) = withAuth { username => implicit request =>
    executeType match {
      case "addMessage" =>
        bindForm(addMessageForm) match {
          case Right(message) =>
            val result = db.run(MessageTable += Message(-1, Option(username.toInt), message.packageId,
              message.transactionId, message.title, message.question, None, Some(0)))
            result.map {r =>
              Ok(Json.obj(
                "Result" -> "OK"
              ))
            }
          case Left(_) =>
            Future.successful(BadRequest(Messages("wrong.form.format.exception")))
        }
    }
  }
}

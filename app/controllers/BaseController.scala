package controllers

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.i18n.Messages
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._

/**
 * Created by mzhang on 2015/10/31.
 */
trait BaseController extends Controller{
  lazy val db = backend.DB.db

  def returnJson(result: JsValue) : Result = {
    (result \ "Result").as[String] match {
      case "OK" => Ok(result)
      case _ => BadRequest(result)
    }
  }
  /**
   * Success Response
   */
  def withSuccessResponse () = Json.obj("Result" -> "OK")
  /**
   * Failed Response
   */
  def withFailedResponse (errorMessage: String) = Json.obj("Result" -> "ERROR", "ErrorMessage" -> errorMessage)
  /**
   * Not define action
   * @return
   */
  def withNotDefineAction () = Json.obj("Result" -> "ERROR", "ErrorMessage" -> Messages("undefined.action.exception"))

  /**
   * withWrongFormFormat
   * @return
   */
  def withWrongFormFormat () = Json.obj("Result" -> "ERROR", "ErrorMessage" -> Messages("wrong.form.format.exception"))


  /**
    * @return
    */
  def bindForm[T](f: play.api.data.Form[T])(implicit r: play.api.mvc.Request[_]) =
    f.bindFromRequest fold(e=> Left(e.errorsAsJson), Right(_))

  implicit def string2jodaTime(s: String): DateTime = DateTimeFormat.forPattern("YYYY-MM-DD HH:mm").parseDateTime(s)
  implicit def jodaTime2string(d: DateTime): String = d.toString("YYYY-MM-DD HH:mm")
}

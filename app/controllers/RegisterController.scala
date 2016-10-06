package controllers

import net.tanesha.recaptcha.ReCaptchaImpl
import org.joda.time.DateTime
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import models.Tables._
import models._
import util.{ReCaptcha, Constants}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import models.tables.MyDriver.api._

class RegisterController extends Controller with BaseController{

  /**
   * Register form
   */
  val registerForm = Form {
    tuple(
      "email" -> text,
      "password" -> text,
      "username" -> text,
      "phone" -> text,
      "mobile" -> text,
      "qq" -> text,
      "id" -> text,
      "recaptcha_challenge_field" -> nonEmptyText,
      "recaptcha_response_field" -> nonEmptyText
    )
  }

  /**
   * @return
   */
  def register = Action { request =>
    Ok(views.html.userviews.register())
  }

  /**
    * check captcha
    * @param addr
    * @param challenge
    * @param response
    * @return
    */
  def check(addr: String, challenge: String, response: String): Boolean = {
    val reCaptcha = new ReCaptchaImpl()
    reCaptcha.setPrivateKey(ReCaptcha.privateKey())
    val reCaptchaResponse = reCaptcha.checkAnswer(addr, challenge, response)
    reCaptchaResponse.isValid()
  }

  /**
   * @return
   */
  def execute(executeType: String)= Action.async { request =>
    executeType match {
      case "add" =>
        val data = registerForm.bindFromRequest()(request).get
        if (!check(request.remoteAddress, data._8, data._9)) {
          Future.successful(Ok("wrong_captcha"))
        } else {
          val user = db.run(UserTable.filter { u => u.email === data._1 }.result.headOption)
          user.map {
            case Some(u) => Ok("registered")
            case _ => {
              val time = new DateTime()
              val sql = UserTable returning UserTable.map(_.id) += User(
                0, // id
                data._1, // email
                data._2, // password
                data._3, // name
                "1", // sex
                Some(data._4), // phone
                Some(data._5), // mobile
                Some(data._6), // qq
                Some(data._7), // ID number
                Option("0"), // isSignUp
                Option(time.toString("YYYY-MM-DD HH:mm")), // Last login time
                Option("0"), // User type
                Option(0.0), // User type
                Option(0.0), // User type
                Option(time.toString("YYYY-MM-DD HH:mm")), // update time
                Option(time.toString("YYYY-MM-DD HH:mm")) // create time
              )

              Await.result(db.run(sql), Duration(Constants.SQL_WAIT_TIME, MILLISECONDS)) match {
                case id: Int => Ok("success")
              }
            }
          }
        }
      //case "update" =>
      //case "delete" =>
      case _ => Future.successful(BadRequest("未定义请求."))
    }
 }
}
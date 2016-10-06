package controllers

import models.Tables._
import models._
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.Messages
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import play.api.libs.Crypto
import scala.concurrent.Future
import models.tables.MyDriver.api._
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global

class LoginController extends Controller with BaseController{
  val userForm = Form(
    mapping(
      "username" -> text,
      "password" -> text,
      "remember_me" -> optional(text)
    )(UserDataForm.apply)(UserDataForm.unapply)
  )

  /**
   * @return
   */
  def execute = Action.async { implicit request =>
    /* binding success, you get the actual value. */
    bindForm(userForm) match {
      case Right(model) =>
        val sql = UserTable.filter(user => user.email === model.username && user.password === model.password).result
        val result: Future[Seq[User]] = db.run(sql)
        val f = result.map(_.headOption)

        f.map {
          case Some(user) =>
            val userId = user.id
            println("here !!",user.userType)


            var redirect = user.userType match {
              case Some("super_admin") | Some("admin") =>
                println("admin matched")
                Redirect("/admin")
              case _ =>
                Redirect("/myAccount")
            }
            if (model.remeber_me.isDefined)
              redirect = redirect.withCookies(Cookie("rememberme", Crypto.sign(model.username) + "-" + model.username, Some(604800)))

            var session = Map(Security.username -> userId.toString, "user" -> user.name)

            if (user.userType.isDefined)
              session += ("user_role" -> user.userType.get)

            redirect.withSession(session.toArray :_*)
          case _ =>
            Ok(views.html.userviews.login(Some(Messages("login.password.wrong"))))
        }
      case Left(_) =>
        Future.successful(Ok(views.html.userviews.login(Some(Messages("login.password.wrong")))))
    }
  }

  /**
   * @return
   */
  def login = Action { request =>
    Ok(views.html.userviews.login(None))
  }

  case class UserDataForm(username: String, password: String, remeber_me: Option[String])
}


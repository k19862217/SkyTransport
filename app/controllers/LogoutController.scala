package controllers

import play.api.i18n.Messages
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import play.api.mvc._

class LogoutController extends Controller with Secured{

  /**
   * @return
   */
  def execute = Action { implicit request =>
    Ok(views.html.userviews.login(Some(Messages("logout.success")))).withNewSession
  }
}
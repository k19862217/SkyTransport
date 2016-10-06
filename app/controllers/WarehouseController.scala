package controllers

import play.api.mvc._

import scala.concurrent.Future

class WarehouseController extends Controller with Secured{
  case class AuthData(username: String, password: String)

  /**
   * @return
   */
  def warehouse = withAuth {username => implicit request =>
    Future.successful(Ok(views.html.userviews.warehouse(username)))
  }
}
package controllers

import play.api.mvc._

import scala.concurrent.Future

/**
  * Created by ming.zhang on 11/17/15.
  */
trait Secured {
  def username(request: RequestHeader) = request.session.get(Security.username)

  def onUnauthorized(request: RequestHeader) = Results.Redirect(controllers.routes.LoginController.login)

  def withAuth(f: => String => Request[AnyContent] => Future[Result]) = {
    Security.Authenticated(username, onUnauthorized) { user =>
      Action.async(request => f(user)(request))
    }
  }
}

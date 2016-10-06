package controllers

/**
  * Created by tedrahedron on 12/18/15.
  */

import play.api.mvc._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object LoginedAction extends ActionBuilder[Request] {
  override def invokeBlock[A](request: Request[A],
                              block: (Request[A]) => Future[Result]): Future[Result] = {

    println(request.session.data)

    request.session.get(Security.username) match {
      case Some(_) =>
        println("passed login")
        block(request)
      case _ => Future{ Results.Redirect(controllers.routes.LoginController.login) }
    }
  }
}

object SuperAdminAction extends ActionBuilder[Request]{
  override def invokeBlock[A](request: Request[A],
                              block: (Request[A]) => Future[Result]): Future[Result] = {
      request.session.get("user_role") match {
        case Some("super_admin") => println("passed super admin")
        block(request)
        case _ => Future{ Results.Redirect(controllers.routes.LoginController.login) }
      }
    }
}

object AdminAction extends ActionBuilder[Request]{
  override def invokeBlock[A](request: Request[A],
                              block: (Request[A]) => Future[Result]): Future[Result] = {
    request.session.get("user_role") match {
      case Some("admin") | Some("super_admin") => println("passed admin")
        block(request)
      case _ => Future{ Results.Redirect(controllers.routes.LoginController.login) }
    }
  }
}


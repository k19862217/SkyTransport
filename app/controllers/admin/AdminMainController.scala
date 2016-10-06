package controllers.admin

/**
  * Created by tedrahedron on 12/15/15.
  */
import play.api.mvc._
import controllers._


class AdminMainController extends Controller {

  def index() = (LoginedAction andThen AdminAction) {
    Ok(views.html.adminviews.admin_main())
  }
}

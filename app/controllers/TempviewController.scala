package controllers

import play.api.mvc._

/**
  * Created by tedrahedron on 2/7/16.
  */
class TempviewController extends Controller {

  def index = Action {
    Ok( views.html.temp() )
  }

}

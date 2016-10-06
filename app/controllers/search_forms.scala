package controllers
import play.api.data._
import play.api.data.Forms._

/**
  * Created by xiaochen.tian on 12/2/15.
  */

case class UserSearchForm(id:Option[Int],name:Option[String],email:Option[String])

object UserSearchForm {
  val form: Form[UserSearchForm] = Form(
    mapping(
      "user_id" -> optional(number),
      "user_name" -> optional(text),
      "email" -> optional(text)
    )(UserSearchForm.apply)(UserSearchForm.unapply)
  )
}

case class PackageSearchForm(id:Option[Int],
                             orderNumber: Option[String],
                             goodsName:Option[String],
                             status:Option[String])

object PackageSearchForm {
  val form = Form(
    mapping(
      "package_id" -> optional(number),
      "package_orderNumber" -> optional(text),
      "goodsName" -> optional(text),
      "package_status" -> optional(text)
    )(PackageSearchForm.apply)(PackageSearchForm.unapply)
  )
}

case class TransactionSearchForm(id:Option[Int],
                                 orderNumber:Option[String],
                                 status:Option[Int])

object TransactionSearchForm {
  val form = Form(
    mapping(
      "transaction_id" -> optional(number),
      "transaction_orderNumber" -> optional(text),
      "transaction_status" -> optional(number)
    )(TransactionSearchForm.apply)(TransactionSearchForm.unapply)
  )
}


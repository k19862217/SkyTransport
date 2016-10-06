package models

import models.Tables._
import play.api.libs.json.Json

/**
 * Created by Akira on 15/11/15.
 */
case class AccountDetail
(
  userInfo: User,
  userSpentInfo: List[Transaction]
)

object AccountDetail {
  implicit val accountDetailFormat = Json.format[AccountDetail]
}

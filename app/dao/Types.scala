package dao

/**
  * Created by xiaochen.tian on 12/2/15.
  */
import models._
import models.Tables._

object Types {
  type Type_package = (Option[User],  PackageInfo, Option[Transaction])

  type Type_user_transaction = (User,  Transaction, AddressInfo)



}

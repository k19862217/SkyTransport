package models

import models.Tables._
import play.api.libs.json.Json

/**
 * Created by mzhang on 2015/11/08.
 */
case class PackageDetail(packages: List[PackageInfo],
                         transactionInfo:Option[Transaction],
                         addressInfo: Option[AddressInfo])
object PackageDetail {
  implicit val formatPackageDetail= Json.format[PackageDetail]
}

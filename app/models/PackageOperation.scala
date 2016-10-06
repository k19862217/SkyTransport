package models

import org.joda.time.DateTime

/**
  * Created by xiaochen.tian on 11/30/15.
  */
case class PackageOperation(id: Int,
                            from_package_id: Int,
                            to_package_id: Int,
                            operation_type: Int,
                            operation_status: Int,
                            operator_id: Option[Int] = Some(0),
                            updateTime: Option[DateTime] = None,
                            createTime: Option[DateTime] = None)

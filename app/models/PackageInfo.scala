package models

import org.joda.time.DateTime

/**
  * Created by xiaochen.tian on 11/30/15.
  */
case class PackageInfo(id: Int,
                       userId: Option[Int] = None,
                       transactionId: Option[Int] = None,
                       warehouseId: Option[Int] = None,
                       goodsName: Option[String] = None,
                       goodsPrice: Option[Double] = None,
                       goodsCount: Option[Int] = None,
                       orderNumber: Option[String] = None,
                       forecastTime: Option[DateTime] = None,
                       description: Option[String] = None,
                       goodsWeight: Option[Double] = None,
                       arrivedTime: Option[DateTime] = None,
                       status: Option[String] = None,
                       operator: Option[String] = None,
                       updateTime: Option[DateTime] = None,
                       createTime: Option[DateTime] = None)


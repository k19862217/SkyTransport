package models

import org.joda.time.DateTime

/**
  * Created by xiaochen.tian on 11/30/15.
  */
case class Transaction(id: Int,
                       userId: Option[Int] = None,
                       addressId: Option[Int] = None,
                       needCombine: Option[String] = None,
                       needDivide: Option[String] = None,
                       needInsurance: Option[String] = None,
                       needCheck: Option[String] = None,
                       needFirm: Option[String] = None,
                       needFastDelivery: Option[String] = None,
                       logisticMethod: Option[String] = None,
                       deliveryCost: Option[Double] = None,
                       insuranceCost: Option[Double] = None,
                       keepCost: Option[Double] = None,
                       operationCost: Option[Double] = None,
                       totalCost: Option[Double] = None,
                       orderNumber: Option[String] = None,
                       sendTime: Option[DateTime] = None,
                       status: Option[Int] = Some(0),
                       updateTime: Option[DateTime] = None,
                       createTime: Option[DateTime] = None)

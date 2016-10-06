package models

/**
  * Created by xiaochen.tian on 11/30/15.
  */
case class WarehouseInfo(id: Int, name: Option[String] = None, country: Option[String] = None, city: Option[String] = None, address: Option[String] = None, phone: Option[String] = None, postcode: Option[String] = None)


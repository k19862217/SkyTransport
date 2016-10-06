package models

/**
  * Created by xiaochen.tian on 11/30/15.
  */

import org.joda.time.DateTime
case class AddressInfo(id: Int,
                       userId: Int,
                       country: Option[String] = None,
                       city: Option[String] = None,
                       address: Option[String] = None,
                       receiver: Option[String] = None,
                       phone: Option[String] = None,
                       postcode: Option[String] = None,
                       visible: Option[String] = None,
                       updateTime: Option[DateTime] = None,
                       createTime: Option[DateTime] = None){

  def shortFormat:String = {
    s"$country $city $address\n"
  }

}


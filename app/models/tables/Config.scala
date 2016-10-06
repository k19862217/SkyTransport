package models.tables

/**
  * Created by xiaochen.tian on 11/30/15.
  */

import slick.driver.MySQLDriver


object MyDriver extends MySQLDriver {
  val support = com.github.tototoshi.slick.MySQLJodaSupport
}


//object MyDriver extends H2Driver

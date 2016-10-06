package dao

import backend.DB
import models.Tables._
import models.{PackageInfo, User}
import models.tables.{PackageTable, UserTable}

import models.tables.MyDriver.api._

/**
  * Created by xiaochen.tian on 12/2/15.
  */
trait BaseDAO {

  def db = backend.DB.db

}

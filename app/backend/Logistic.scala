package backend

/**
  * Created by tedrahedron on 2/17/16.
  */

import dao._
import models._
import models.tables.{PackageTable, UserTable}
import backend.DB
import models.tables.MyDriver.api._
import models.tables.MyDriver.support._
import models.Tables._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import util.Formatters._


object Logistic {

  def ship(transaction: Transaction,update_transaction:Boolean = true) = {
    lazy val update1 = PackageTable.filter( _.transactionId === transaction.id).map(_.status).update( Some("3") )
    val update2 = TransactionTable.filter( _.id === transaction.id).map(_.status).update( Some(2) )
    val seq = if (update_transaction) DBIO.seq( update1,update2 ) else DBIO.seq( update1 )
    DB.db.run( seq )
  }

}

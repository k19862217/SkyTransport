package dao

/**
  * Created by xiaochen.tian on 12/3/15.
  */

import backend.DB
import controllers.UserSearchForm
import models.Tables._
import models.{Transaction, PackageInfo, User}
import models.tables.MyDriver.api._
import models.tables.MyDriver.support._
import models.tables._
import scala.concurrent.ExecutionContext.Implicits.global

object UserDAO extends BaseDAO{


  def get_search_condition_opt(rep:Rep[Option[UserTable]],form:UserSearchForm) = {
    var result:Rep[Option[Boolean]] = Some(true)

    if (form.id.isDefined){
      result = result && (rep.map(_.id) === form.id.get)
    }

    if (form.name.isDefined){
      result = result && (rep.map(_.name) === form.name.get)
    }

    if (form.email.isDefined){
      result = result && (rep.map(_.email) === form.email.get)
    }

    result
  }


  def get_search_condition(rep:UserTable,form:UserSearchForm) = {
    var result:Rep[Option[Boolean]] = Some(true)

    if (form.id.isDefined){
      result = result && (rep.id === form.id.get)
    }

    if (form.name.isDefined){
      result = result && (rep.name === form.name.get)
    }

    if (form.email.isDefined){
      result = result && (rep.email === form.email.get)
    }

    result
  }

  def get_balance(userId:Int) = {
    db.run(UserTable.filter(_.id === userId).map(_.balance).result) map (_.headOption)
  }

  def update_balance(userId:Int, balance: Double) = {
    db.run(UserTable.filter(_.id === userId).map(_.balance).update(Option(balance)))
  }

}

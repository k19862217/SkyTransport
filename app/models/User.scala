package models

import org.joda.time.DateTime

/**
  * Created by xiaochen.tian on 11/30/15.
  */
case class User(id: Int,
                email: String,
                password: String,
                name: String,
                sex: String,
                phone: Option[String] = None,
                mobile: Option[String] = None,
                qq: Option[String] = None,
                idNumber: Option[String] = None,
                isSignUp: Option[String] = None,
                lastLoginTime: Option[DateTime] = None,
                userType: Option[String] = None,
                totalSpent: Option[Double] = None,
                balance: Option[Double] = None,
                updateTime: Option[DateTime] = None,
                createTime: Option[DateTime] = None)

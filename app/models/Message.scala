package models

import org.joda.time.DateTime

/**
  * Created by xiaochen.tian on 11/30/15.
  */
case class Message(id: Int,
                   from: Option[Int] = None,
                   packageId: Option[Int] = None,
                   transactionId: Option[Int] = None,
                   title: Option[String] = None,
                   question: Option[String] = None,
                   answer: Option[String] = None,
                   status: Option[Int] = Some(0),
                   updateTime: Option[DateTime] = None,
                   createTime: Option[DateTime] = None)

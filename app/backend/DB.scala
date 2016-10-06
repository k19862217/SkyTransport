package backend

/**
  * Created by xiaochen.tian on 11/30/15.
  */
import models.tables.MyDriver.api._
import models.Tables._
import org.joda.time.format.DateTimeFormat

import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import models._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}


object DB {

  //var url = "jdbc:mysql://localhost/zhuanyun"
  var url = "jdbc:mysql://ec2-54-249-90-223.ap-northeast-1.compute.amazonaws.com:3306/skyzhuanyun"
  //var url = ("jdbc:h2:tcp://localhost/mem:test;DB_CLOSE_DELAY=-1")
  //val url = ("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
  //val url = ("jdbc:h2:~/h2db_test;DB_CLOSE_DELAY=-1")

  //lazy val db = Database.forURL(url,driver = "com.mysql.jdbc.Driver",user = "user1",password="abcd1234")
  lazy val db = Database.forURL(url,driver = "com.mysql.jdbc.Driver",user = "skyzhuanyun",password="x8prztsw")

  def print_db = {
    println(db)
  }

  def start_H2_server() = {
    import play.api.libs.concurrent.Execution.Implicits._
    Future {
      import org.h2.tools.Server

      val server = Server.createTcpServer("-tcpAllowOthers")

      println(s"H2 TCP server started URL: ${server.getURL}")
      server.start

    }
  }

  def add_some_data():Unit = {
    println("add_some_data")
    val dateTime = DateTimeFormat.forPattern("YYYY-MM-DD HH:mm").parseDateTime("2015-12-12 11:00")
    val actions = db.run{DBIO.seq(
      UserTable += User(-1,"abc@gmail.com","abc","abc","male",None, None, None, None, None, None, None, Some(0), Some(1000000), None, None),
      UserTable += User(-1,"123@gmail.com","123","123","female", None, None, None, None, None, None, None, Some(0), Some(1000000), None, None),
      UserTable += User(-1,"user1@gmail.com","abc","user1","female" ,None, None, None, None, None, None, None, Some(0), Some(1000000), None, None),
      UserTable += User(-1,"admin@gmail.com","admin","admin1","female" ,None, None, None, None, None, None, Some("admin"), Some(0), Some(1000000), None, None),
      UserTable += User(-1,"sadmin@gmail.com","sadmin","super admin","female" ,None, None, None, None, None, None, Some("super_admin"), Some(0), Some(1000000), None, None),
      WarehouseTable.forceInsert(WarehouseInfo(1,Some("Tokyo warehouse"),address = Some("My home"))),
      MessageTable += Message(1, Some(1), None, None, Some("我的货怎么还没到"), Some("我的货怎么还没到 ???"), Some("不知道"), Some(1), Some(dateTime), Some(dateTime)),
      MessageTable += Message(1, Some(1), None, None, Some("我的货怎么还没到"), Some("我的货怎么还没到 ???"), Some("不知道"), Some(0), Some(dateTime), Some(dateTime))
    )}

    actions onComplete{
      case Success(_) => println("sample data successfully created")
      case Failure(t) =>
        println("sample data  failed")
        println(t.getMessage)
    }

    Await.ready( actions ,100 seconds)

    Await.ready( db.run(UserTable.result).map{ x => x foreach println; println("#user: ",x.length) } ,100 seconds)

  }

  def database_drop():Unit = {

    val actions = db.run(DBIO.seq(
      AddressTable.schema.drop,
      PackageTable.schema.drop,
      TransactionTable.schema.drop,
      UserTable.schema.drop,
      WarehouseTable.schema.drop,
      MessageTable.schema.drop,
      PackageOperationTable.schema.drop
    ))

    Await.ready(actions,100 seconds)

  }

  def database_create():Unit = {

    val actions = db.run(DBIO.seq(
      AddressTable.schema.create,
      PackageTable.schema.create,
      TransactionTable.schema.create,
      UserTable.schema.create,
      WarehouseTable.schema.create,
      MessageTable.schema.create,
      PackageOperationTable.schema.create
    ))

    actions onComplete{
      case Success(_) => println("table successfully created")
      case Failure(t) =>
        println("table creation failed")
        println(t.getMessage)
    }

    Await.ready(actions,100 seconds)

  }

  def create_trigger_mysql() = {
    println("create trigger for mysql")
    var sqls = List[String]()
    sqls ::=
      """
        create DEFINER = CURRENT_USER TRIGGER `zhuanyun`.`package_table_BEFORE_INSERT` BEFORE INSERT ON `package_table` FOR EACH ROW
        BEGIN
        SET NEW.Create_time = NOW();
          SET NEW.UPDATE_TIME = NOW();
        END
      """
    sqls ::=
      """
        CREATE DEFINER = CURRENT_USER TRIGGER `zhuanyun`.`package_table_AFTER_UPDATE` BEFORE UPDATE ON `package_table` FOR EACH ROW
        BEGIN
          set new.update_time = NOW();
        END
      """
    sqls ::=
      """
        create DEFINER = CURRENT_USER TRIGGER `zhuanyun`.`address_table_BEFORE_INSERT` BEFORE INSERT ON `address_table` FOR EACH ROW
        BEGIN
        SET NEW.Create_time = NOW();
          SET NEW.UPDATE_TIME = NOW();
        END
      """
    sqls ::=
      """
        CREATE DEFINER = CURRENT_USER TRIGGER `zhuanyun`.`address_table_AFTER_UPDATE` BEFORE UPDATE ON `address_table` FOR EACH ROW
        BEGIN
          set new.update_time = NOW();
        END
      """
    sqls ::=
      """
        create DEFINER = CURRENT_USER TRIGGER `zhuanyun`.`message_table_BEFORE_INSERT` BEFORE INSERT ON `message_table` FOR EACH ROW
        BEGIN
        SET NEW.Create_time = NOW();
          SET NEW.UPDATE_TIME = NOW();
        END
      """
    sqls ::=
      """
        CREATE DEFINER = CURRENT_USER TRIGGER `zhuanyun`.`message_table_AFTER_UPDATE` BEFORE UPDATE ON `message_table` FOR EACH ROW
        BEGIN
          set new.update_time = NOW();
        END
      """
    sqls ::=
      """
        create DEFINER = CURRENT_USER TRIGGER `zhuanyun`.`transaction_table_BEFORE_INSERT` BEFORE INSERT ON `transaction_table` FOR EACH ROW
        BEGIN
        SET NEW.Create_time = NOW();
          SET NEW.UPDATE_TIME = NOW();
        END
      """
    sqls ::=
      """
        CREATE DEFINER = CURRENT_USER TRIGGER `zhuanyun`.`transaction_table_AFTER_UPDATE` BEFORE UPDATE ON `transaction_table` FOR EACH ROW
        BEGIN
          set new.update_time = NOW();
        END
      """
    sqls ::=
      """
        create DEFINER = CURRENT_USER TRIGGER `zhuanyun`.`user_table_BEFORE_INSERT` BEFORE INSERT ON `user_table` FOR EACH ROW
        BEGIN
        SET NEW.Create_time = NOW();
          SET NEW.UPDATE_TIME = NOW();
        END
      """
    sqls ::=
      """
        CREATE DEFINER = CURRENT_USER TRIGGER `zhuanyun`.`user_table_AFTER_UPDATE` BEFORE UPDATE ON `user_table` FOR EACH ROW
        BEGIN
          set new.update_time = NOW();
        END
      """

    val f = db.run(SimpleDBIO { x =>
      try {
        sqls.foreach(x.connection.createStatement().execute)
      } catch {
        case x:Throwable => println("error",x)
      }
    })

    Await.ready(f,100 seconds)

  }

  def database_reset():Unit = {

  }

}

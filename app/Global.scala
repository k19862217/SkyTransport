/**
  * Created by xiaochen.tian on 11/30/15.
  */
import play.api.{Application, GlobalSettings}
import play.api.Play.current

/**
  * Created by xiaochen.tian on 11/19/15.
  */
object Global extends GlobalSettings{


  override def onStart(app: Application): Unit = {
    println("Global on start")
    backend.DB.print_db

    return

    backend.DB.database_drop()

    backend.DB.database_create()
    backend.DB.create_trigger_mysql()
    backend.DB.add_some_data()
    //backend.DB.start_H2_server()



  }
}

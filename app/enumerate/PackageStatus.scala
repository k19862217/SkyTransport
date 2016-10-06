package enumerate

/**
  * Created by xiaochen.tian on 12/1/15.
  */
object PackageStatus {

  val jsonAllStatus:String = ""
  val jsonStatusMap:String = ""

  val mapping = Map(
    "0" -> "未入库"
    ,"1" -> "入库未提交"
    ,"2" -> "提交未发货"
    ,"3" -> "已发货"
    ,"4" -> "被拆分"
    ,"5" -> "被管理员删除"
    ,"6" -> "丢失"
    ,"7" -> "被合箱"
  )

  val reversed_mapping = mapping map { case (k,v) => v -> k }

  val allStatus = mapping.keys.toSeq.sorted

  def print(status:Option[String]):String = print(status.get)

  def print(status:String):String = mapping.getOrElse(status,"undefined")

}

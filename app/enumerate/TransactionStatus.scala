package enumerate

/**
  * Created by xiaochen.tian on 12/8/15.
  */
object TransactionStatus {

  val mapping = Map(
     0 -> "未付款"
    ,1 -> "已付款 未发货"
    ,2 -> "已付款 已发货"
    ,3 -> "已送达"
  )

  val reversed_mapping = mapping map { case (k,v) => v -> k }

  val allStatus = mapping.keys.toSeq.sorted

  def print(status:Option[Int]):String = print(status.get)

  def print(status:Int):String = mapping.getOrElse(status,"undefined")


}

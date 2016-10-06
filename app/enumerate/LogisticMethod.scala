package enumerate

/**
  * Created by tedrahedron on 2/17/16.
  */
object LogisticMethod {

  val mapping = Map(
     "t1" -> "EMS原箱线"
    ,"t2" -> "EMS特快线"
    ,"t3" -> "SAL空陆联运"
    ,"t4" -> "海运（船运）"
    ,"t5" -> "AIR航空线"
    ,"t6" -> "其他"
  )

  val reversed_mapping = mapping map { case (k,v) => v -> k }

  val allStatus = mapping.keys.toSeq.sorted

  def print(deliveryMethod:String):String = mapping(deliveryMethod)

  def print(deliveryMethod:Option[String]):String = if (deliveryMethod.isDefined) print(deliveryMethod.get) else ""

}

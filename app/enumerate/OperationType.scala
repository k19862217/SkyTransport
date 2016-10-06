package enumerate

/**
  * Created by ming.zhang on 2/21/16.
  */
object OperationType {
  val PACKAGE_ORIGIN = 0
  val PACKAGE_COMBINE = 1
  val PACKAGE_DIVIDE = 2

  val mapping = Map(
    PACKAGE_ORIGIN -> "原箱"
    ,PACKAGE_COMBINE -> "合箱"
    ,PACKAGE_DIVIDE -> "分箱"
  )

  val reversed_mapping = mapping map { case (k,v) => v -> k }

  val allOperationType = mapping.keys.toSeq.sorted

  def print(operation:Option[Int]):String = print(operation.get)

  def print(operation:Int):String = mapping.getOrElse(operation,"undefined")


}

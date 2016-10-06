package util

import org.joda.time.ReadableInstant
import play.api.data.FormError
import play.api.data.format.Formatter
import org.joda.time.format.DateTimeFormat

/**
  * Created by tedrahedron on 2/9/16.
  */
object Formatters {

  def StringBoolFormatter: Formatter[String] = new Formatter[String] {

    def bind(key: String, data: Map[String, String]): Either[Seq[FormError], String] = {

      println("here",data.get(key))

      Right(data.get(key).getOrElse("false")).right.flatMap {
        case "on" => Right("1")
        case _ => Right("0")
      }
  }

    override def unbind(key: String, value: String): Map[String, String] =  Map(key -> value.toString)
  }


  val date_pattern_string = "yyyy-MM-dd HH:mm"

  val clock_pattern = DateTimeFormat.forPattern("HH:mm")
  val date_pattern = DateTimeFormat.forPattern("yyyy-MM-dd")
  val time_pattern = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm")



  def parseDateTime = time_pattern.parseDateTime _

  def printDateTime(instant:ReadableInstant) = time_pattern.print(instant)


}

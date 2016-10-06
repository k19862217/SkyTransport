package util

import java.util.Properties
import net.tanesha.recaptcha.ReCaptchaFactory
import play.api.Play.current

/**
  * Created by ming.zhang on 2/12/16.
  */
object ReCaptcha {

  def publicKey(): String = {
    current.configuration.getString("recaptcha.publickey").get
  }
  def privateKey(): String = {
    current.configuration.getString("recaptcha.privatekey").get
  }
  def render(): String = {
    ReCaptchaFactory.newReCaptcha(publicKey(), privateKey(), false).createRecaptchaHtml(null, new Properties)
  }
}

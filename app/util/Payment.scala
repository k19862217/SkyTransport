package util

import java.util

import _root_.util.alipay.config.AlipayConfig
import _root_.util.alipay.util.AlipaySubmit
import models.PaymentInfo

/**
  * Created by ming.zhang on 2/2/16.
  */
object Payment {
  def pay(paymentInfo: PaymentInfo) : String = {
    //把请求参数打包成数组
    val sParaTemp = new util.HashMap[String, String]()
    sParaTemp.put("service", "create_direct_pay_by_user")
    sParaTemp.put("partner", AlipayConfig.partner)
    sParaTemp.put("seller_email", AlipayConfig.seller_email)
    sParaTemp.put("_input_charset", AlipayConfig.input_charset)
    sParaTemp.put("payment_type", paymentInfo.paymentType)
    sParaTemp.put("notify_url", paymentInfo.notifyUrl)
    sParaTemp.put("return_url", paymentInfo.returnUrl)
    sParaTemp.put("out_trade_no", paymentInfo.outTradeNo)
    sParaTemp.put("subject", paymentInfo.subject)
    sParaTemp.put("total_fee", paymentInfo.totalFee)
    sParaTemp.put("body", paymentInfo.body)
    sParaTemp.put("show_url", paymentInfo.showUrl)
    sParaTemp.put("anti_phishing_key", paymentInfo.antiPhishingKey)
    sParaTemp.put("exter_invoke_ip", paymentInfo.exterInvokeIp)

    //建立请求
    val sHtmlText = AlipaySubmit.buildRequest(sParaTemp,"get","确认")
    sHtmlText
  }
}

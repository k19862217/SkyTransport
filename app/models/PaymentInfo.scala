package models

/**
  * Created by ming.zhang on 2/2/16.
  */
case class PaymentInfo
(
  //支付类型
  paymentType: String,

  //必填，不能修改
  //服务器异步通知页面路径
  notifyUrl: String,

  //页面跳转同步通知页面路径
  returnUrl: String,

  //商户订单号
  outTradeNo: String,

  //订单名称
  subject: String,

  //付款金额
  totalFee: String,

  //订单描述
  body: String,

  //商品展示地址
  showUrl: String,

  //防钓鱼时间戳
  antiPhishingKey: String,

  //客户端的IP地址
  exterInvokeIp: String

)

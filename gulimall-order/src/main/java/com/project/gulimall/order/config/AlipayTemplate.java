package com.project.gulimall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.project.gulimall.order.domain.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private   String app_id = "9021000152656302";
    // 商户私钥，您的PKCS8格式RSA2私钥
    private  String merchant_private_key = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCDqtuNkx6AZmLY8XD8q1O202DVadWJp2IdnUNFxck2t7vHn3yasa0E7MO16CNKzOI4c9joo1crXHxtfoBFK8Qk2JJdAo4s4JKhtn6CnCXIaOKBQ/ZRQHFvpr4fWdnVQQgKceQV72yS330kt/3wPPPMaqcJL+8oNrWcWkGK96rPcGxlJpBCrHhdUB3VAwpZC4acmUVHbxAnuRsWDoQewgI4Xe6J1yXhSiCd948gjONsSXKmm3iZZQlR445vuHyjOPTaNiTWpBmEUZN60BYKXuUh6M2S8XwR3BCkB7/r9OTWneKc/VRwrgN/zA4SFPeHYpV5X/1TPFdxwgUO58s04h+JAgMBAAECggEAXsh+aLpBuNj7y4Rze9Cx4Ojlynv3lrKCNSNirDWnldZKPXgYMRw6m1L9yFOmJFC9gToUKdR8CeD4SbJJEIJjHssxAfe29aNsqzE+fTN/F3g5piiQhwlHH8L/Fn6OC7BW4339XbUPieOMqQQyr+CQ+NTGDh0NovtPXZCzoiCMO+uAJ26FQ82xDnCJ1w+tmkq8By99ZdSTQ7ckANbq3kncDe4XoQvhUxYKPIa+4sdtf33rwIPzAXOkzZboh6IcFrtqZxwWzx700tZkzFcOB48wa5FkSJ8Dsk9AtMzZIjIUKtgFEt2hry8apU/J0hPVDzJPRa6f6kFxNRYsBm4HaJ5IoQKBgQC4HOWayQC7gFwWZJ7SKzLdRxmvBd5v301ltqN6wjQ0wEbOWMy9kswWsDkCkvGO5DapcN0BIRQ1Z9I3dRzFhOMlhv1kFa602O3ZzXpiOuCZoTMZJgHYLgqSEfezmSCs6rL8oCAZSM9bfm474VPiS6JcYTRGy+kYOxaNUx+/C19pPQKBgQC3E77geVGBWOFceH11c9qlTuGHGU1gwvRMvmTzLCXSotOlrqO1RHULtJbzjpTvwsysBnCEAgUQo2iX5U/peDfqduFhOSxUN9ugOVVBY5zbEQycz3WD3ii16EF7UUJ3mV9QU6XekYlS6JO1YLPY+YKyk9i52wX1ThfH1/GEDqb8PQKBgQCkSxURBOEkcKy8Rtn7DhV7pGDk8EXIaun0JADKINbZY+NLa654VLDOZj7Zbysjqb6lgVOWGGCiL51FY7pi/+x6pnUjhL28IABP5a6aTZPzRAgHHwVyVdOU+Xeiyrh/1YgXKwS5y2FOcgoIYVCrlXazHQK7UmcU+lVrk4u2vX1MuQKBgCr96g8QrkEvvAxZBy0zvZ6gPXnaST91yKTU+SPZtDAYqJb5wdvpbYsIJ4Kecv8ywZmMEZQOXV4g4Yj6AqAS6R6YOCj6ohxM2bhwfkLSv5z6DfotBa2n1+uP1QC+fltTmvxkCEmR56uejkFDqjhDr5t7+KL8ehO2+QKnBUI7pp8JAoGAGGgeIZQEjiV46gebzgSBntY244yFsDkn7jR9M63VgoP48O1v+7kRvHMSanxiXks1zDtLNDhwmApQ9atOW2JiM4RcA19Sbdl35mvyEyAlrjOESH+EkIWCRtZD/nYk+i5F7lNL891QrydKj3ovsJNZUwWFYP+m0+CJDBYa3w70t44=";
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private  String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtw1GMxtZQjm5ommUTACyosujDtB/qACIUwW16TbGiOa4gKAqfuP9AGmtVdjpEe6kMs1DANbxM6MbSE03Dh0G4MUylBBu/uLwlkazrSigLKWh4qnID9+1IKYb5FZk0NH7Ibi+MSWQKRUo3u4XOTSQjcPTnBpcAaj9AI8OvgYNYMiJBHNj6PVMqajp25AyRUAMur3cb6nAsZfXUOvBpncTr7nuAMYID+r9g3Xw3dlkUJ09CKx5i7HhlS5/cvo/fEPewyFxhyd707Q6K2h12JfXXNdMchwBtNfP8QY0RDreSIJK2/VdfOOdeCAQ22oGhSoRsPylQt/YOdcKQ6+gRFLwAwIDAQAB";
    // 服务器[异步通知]页面路径，需 http:// 格式的完整路径，不能加 ?id=123 这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private  String notify_url = "http://1fwcz13643342.vicp.fun/order/payed/notify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 同步通知，支付成功，一般跳转到成功页
    private  String return_url = "http://member.gulimall.com/memberOrder.html";

    // 签名方式
    private  String sign_type = "RSA2";

    // 字符编码格式
    private  String charset = "utf-8";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private  String gatewayUrl = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";

    //
    private String timeout = "30m";

    public  String pay(PayVo vo) throws AlipayApiException {
        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"timeout_express\":\"1m\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应："+result);

        return result;
    }
}

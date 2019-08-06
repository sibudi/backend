package com.yqg.service;

import com.yqg.common.constants.SysParamContants;
import com.yqg.common.utils.DateUtils;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.SmsCodeMandaoUtil;
import com.yqg.service.system.service.SysParamService;
import com.yqg.service.user.response.CheakSmsBalanceResponse;
import com.yqg.utils.MailUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by wanghuaizhou on 2018/1/2.
 */
@Service
@Slf4j
public class CheakSmsBalanceService {

    // 提交还款申请url
    @Value("${sms.cheakSmsBalanceUrl}")
    private String CHEAK_SMS_BALANCE_URL;

    @Autowired
    private SysParamService sysParamService;

    @Autowired
    private JavaMailSender mailSender;  //自动注入的Bean
    @Autowired
    private JavaMailSenderImpl sender;

    @Value("${spring.mail.username}")
    private String Sender; //读取配置文件中的参数

    @Autowired
    private MailUtil mailUtil;

    @Autowired
    private OkHttpClient httpClient;
    /**
     //     *   查询短信账户余额
     //     * */
    public void cheakSmsAccountBalanceEveryHour(boolean isFor) throws Exception{

        try {
            sendHttpRequest(isFor);
        }catch (Exception e){
            log.info("查询短信账户异常");
            e.getStackTrace();
            if (e.getMessage().equals("timeout")){
                log.info("请求超时，重新发送请求");
                sendHttpRequest(isFor);
            }
        }

    }

    public void sendHttpRequest(boolean isFor) throws Exception{

        String smsType = "";
        BigDecimal zenzivaBalance = BigDecimal.ZERO;
        BigDecimal rajaBalance = BigDecimal.ZERO ;
        BigDecimal ginotaBalance = BigDecimal.ZERO ;
        BigDecimal twilioNum = BigDecimal.ZERO;

        for (int i = 0;i < 4;i++){
            if (i == 0){
                smsType = "ZENZIVA";
                Request request = new Request.Builder()
                        .url(CHEAK_SMS_BALANCE_URL + smsType)
                        .header("X-AUTH-TOKEN", "DO-IT-CHEAK-SMS")
                        .build();

                Response response = httpClient.newCall(request).execute();
                if(response.isSuccessful())
                {
                    String  responseStr = response.body().string();
                    // 查询短信账户余额
                    log.info("查询ZENZIVA短信账户余额 请求后返回:{}", JsonUtils.serialize(responseStr));
                    CheakSmsBalanceResponse balanceResponse = JsonUtils.deserialize(responseStr,CheakSmsBalanceResponse.class);
                    if (balanceResponse.getCode().equals("0")){
                        zenzivaBalance = new BigDecimal(balanceResponse.getBalance());
                    }
                }
            }else if (i == 1){
                smsType = "RAJA";
                Request request = new Request.Builder()
                        .url(CHEAK_SMS_BALANCE_URL + smsType)
                        .header("X-AUTH-TOKEN", "DO-IT-CHEAK-SMS")
                        .build();

                Response response = httpClient.newCall(request).execute();
                if(response.isSuccessful())
                {
                    String  responseStr = response.body().string();
                    // 查询短信账户余额
                    log.info("查询RAJA短信账户余额 请求后返回:{}", JsonUtils.serialize(responseStr));
                    CheakSmsBalanceResponse balanceResponse = JsonUtils.deserialize(responseStr,CheakSmsBalanceResponse.class);
                    if (balanceResponse.getCode().equals("0")){
                        rajaBalance = new BigDecimal(balanceResponse.getBalance());
                    }
                }
//                rajaBalance = 20000f ;
            }else if (i == 2){
                smsType = "GINOTA";
                Request request = new Request.Builder()
                        .url(CHEAK_SMS_BALANCE_URL + smsType)
                        .header("X-AUTH-TOKEN", "DO-IT-CHEAK-SMS")
                        .build();

                Response response = httpClient.newCall(request).execute();
                if(response.isSuccessful())
                {
                    String  responseStr = response.body().string();
                    // 查询短信账户余额
                    log.info("查询GONOTA短信账户余额 请求后返回:{}", JsonUtils.serialize(responseStr));
                    CheakSmsBalanceResponse balanceResponse = JsonUtils.deserialize(responseStr,CheakSmsBalanceResponse.class);
                    if (balanceResponse.getCode().equals("0")){
                        ginotaBalance = new BigDecimal(balanceResponse.getBalance());
                    }
                }
            }else if (i == 3){
                smsType = "TWILIO";
                Request request = new Request.Builder()
                        .url(CHEAK_SMS_BALANCE_URL + smsType)
                        .header("X-AUTH-TOKEN", "DO-IT-CHEAK-SMS")
                        .build();

                Response response = httpClient.newCall(request).execute();
                if(response.isSuccessful())
                {
                    String  responseStr = response.body().string();
                    // 查询短信账户余额
                    log.info("查询TWILIO短信账户余额 请求后返回:{}", JsonUtils.serialize(responseStr));
                    CheakSmsBalanceResponse balanceResponse = JsonUtils.deserialize(responseStr,CheakSmsBalanceResponse.class);
                    if (balanceResponse.getCode().equals("0")){
                        twilioNum = new BigDecimal(balanceResponse.getBalance());
                    }
                }
            }
        }

        if (isFor){
            //  每小时查询  到达阈值才发送邮件和短信
            if (zenzivaBalance.compareTo(BigDecimal.valueOf(200000))<0 ||
                    rajaBalance.compareTo(BigDecimal.valueOf(100000))<0||
                    ginotaBalance.compareTo(BigDecimal.valueOf(100000))<0){
                sendDataToUser(zenzivaBalance,rajaBalance,ginotaBalance,twilioNum);
            }
        }else {
            //  指定时间 发送邮件和短信
            sendDataToUser(zenzivaBalance,rajaBalance,ginotaBalance,twilioNum);
        }
    }

    //  发送短信和邮件到指定账户
    public void sendDataToUser(BigDecimal balance1,BigDecimal balance2,BigDecimal balance3,BigDecimal balance4) {


        long start = System.currentTimeMillis();

        String receivers = sysParamService.getSysParamValue(SysParamContants.MAIL_RECEIVERS_SMSBALANCE);
        if (receivers == null || receivers.equals("")){
            receivers = "wanghuaizhou@yishufu.com,luhong@yishufu.com,caomiaoke@yishufu.com";
        }
        String content = "";

        log.info("查询短信账户余额邮件定时发送");
        content = mailContent(balance1,balance2,balance3,balance4);

        String mailTitle = "Do-It短信账户余额";

        mailUtil.sendHtmlMail(content,receivers,mailTitle);
        String mobiles = sysParamService.getSysParamValue(SysParamContants.SMS_RECEIVERS_SMSBALANCE);
        if (receivers == null || receivers.equals("")){
            mobiles = "17610156636,15821641051";
        }
        BigDecimal cost = balance4.multiply(BigDecimal.valueOf(0.0259));
        String smsContont = "Do-It 短信平台ZENZIVA,账户余额：" +balance1+
                "；\n短信平台RAJA,账户余额：" +balance2+
                "；\n短信平台GINOTA,账户余额："+balance3+
                "；\n短信平台Twilio,本月使用金额(单位:$):"+cost+"；";
        SmsCodeMandaoUtil.sendSmsCode(mobiles,smsContont);

        log.info("查询短信账户余额邮件定时发送完成{}",System.currentTimeMillis()-start);
    }


    public String mailContent(BigDecimal balance1,BigDecimal balance2,BigDecimal balance3,BigDecimal balance4){

        StringBuffer sb = new StringBuffer();
        Date today = new Date();
        Date chinaDate = DateUtils.addDateWithHour(today,1);
        String todayStr = DateUtils.DateToString2(chinaDate);
        // 邮件前面的说明
        sb.append("<p style='color:#F00'>统计时间截止到中国时间  " + todayStr + "</p>");

        // 各短信平台账户余额
        sb.append("<h3>Do-It各短信平台账户余额(TWILIO为本月使用的金额)</h3>");
        // 表1表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\" width=\"400px\">");
        sb.append("<tr>")
                .append("<th>").append("    短信平台    ")
                .append("<th>").append("    余额阈值    ")
                .append("<th>").append("短信账户余额/条数")
                .append("<th>").append("   是否低于阈值  ")
        ;

        String css = "";
        if (balance1.compareTo(BigDecimal.valueOf(200000))<0 ){
            css = "<td align=\"center\" style='color:#F00'>";
        }else {
            css = "<td align=\"center\">";
        }

        sb.append("<tr>")
                .append("<td align=\"center\">").append("ZENVIRA")
                .append("<td align=\"center\">").append("200000条")
                .append("<td align=\"center\">").append(balance1)
                .append(css).append( balance1.compareTo(BigDecimal.valueOf(200000))<0  ? "是":"否")
        ;
        if (balance2.compareTo(BigDecimal.valueOf(100000))<0 ){
            css = "<td align=\"center\" style='color:#F00'>";
        }else {
            css = "<td align=\"center\">";
        }
        sb.append("<tr>")
                .append("<td align=\"center\">").append("RAJA")
                .append("<td align=\"center\">").append("200000rp")
                .append("<td align=\"center\">").append(balance2)
                .append(css).append( balance2.compareTo(BigDecimal.valueOf(100000))<0 ? "是":"否")
        ;
        if (balance3.compareTo(BigDecimal.valueOf(100000))<0 ){
            css = "<td align=\"center\" style='color:#F00'>";
        }else {
            css = "<td align=\"center\">";
        }
        sb.append("<tr>")
                .append("<td align=\"center\">").append("GINOTA")
                .append("<td align=\"center\">").append("100000rp")
                .append("<td align=\"center\">").append(balance3)
                .append(css).append( balance3.compareTo(BigDecimal.valueOf(100000))<0  ? "是":"否")
        ;

        BigDecimal cost = balance4.multiply(BigDecimal.valueOf(0.0259));
        if (cost.compareTo(BigDecimal.valueOf(17500))>=0 ){
            css = "<td align=\"center\" style='color:#F00'>";
        }else {
            css = "<td align=\"center\">";
        }
        sb.append("<tr>")
                .append("<td align=\"center\">").append("TWILIO")
                .append("<td align=\"center\">").append("11500$")
                .append("<td align=\"center\">").append(cost.setScale(2, RoundingMode.HALF_UP))
                .append(css).append( cost.compareTo(BigDecimal.valueOf(17500))>=0 ? "是":"否")
        ;
        sb.append("</table>");
        return sb.toString();
    }

}

package com.yqg.service;

import com.alibaba.fastjson.JSONObject;
import com.yqg.common.constants.SysParamContants;
import com.yqg.common.utils.DateUtils;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.SmsCodeMandaoUtil;
import com.yqg.service.system.service.SysParamService;
import com.yqg.utils.MailUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 *   查询xdenit 放款账户余额
 * Created by wanghuaizhou on 2018/1/27.
 */
@Service
@Slf4j
public class CheakLoanBalanceService {

    // 提交还款申请url
    @Value("${pay.cheakLoanBalanceUrl}")
    private String CHEAK_LOAN_BALANCE_URL;
    @Value("${pay.token}")
    private String PAY_TOKEN;
    @Autowired
    private OkHttpClient httpClient;

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

    /**
     //     *   查询放款账户余额
     //     * */
    public void cheakLoanAccountBalanceEveryHour(boolean isFor) throws Exception{

        try {
            sendHttpRequest(isFor);
        }catch (Exception e){
            log.info("查询放款账户余额异常");
            e.getStackTrace();
        }

    }

    public void sendHttpRequest(boolean isFor) throws Exception{


        BigDecimal loanBalance = BigDecimal.ZERO ;

        RequestBody requestBody = new FormBody.Builder()
                .add("AccountType","CASH")  // 账户类型
                .build();

        Request request = new Request.Builder()
                .url(CHEAK_LOAN_BALANCE_URL)
                .post(requestBody)
                .header("X-AUTH-TOKEN", PAY_TOKEN)
                .build();

        Response response = httpClient.newCall(request).execute();
        if(response.isSuccessful())
        {
            String  responseStr = response.body().string();
            // 查询放款账户余额
            log.info("查询xendit 放款账户余额 请求后返回:{}", JsonUtils.serialize(responseStr));
            JSONObject res = JSONObject.parseObject(responseStr);
            if (res.get("code").equals("0")){
                JSONObject date = JSONObject.parseObject(res.get("data").toString());
                loanBalance =  new BigDecimal(date.get("balance").toString());
            }
        }

        if (isFor){
            //  每小时查询  到达阈值才发送邮件和短信

            if (loanBalance.compareTo(BigDecimal.valueOf(200000000)) <=0){
                sendDataToUser(loanBalance);
            }
        }else {
            //  指定时间 发送邮件和短信
            sendDataToUser(loanBalance);
        }
    }

    //  发送短信和邮件到指定账户
    public void sendDataToUser(BigDecimal balance) {


        long start = System.currentTimeMillis();

        String receivers = sysParamService.getSysParamValue(SysParamContants.MAIL_RECEIVERS_LOANBALANCE);
        if (receivers == null || receivers.equals("")){
//            receivers = "wanghuaizhou@yishufu.com,luhong@yishufu.com,caomiaoke@yishufu.com";
            receivers = "wanghuaizhou@yishufu.com";
        }
        String content = "";

        log.info("查询XENDIT放款账户余额邮件定时发送");
        content = mailContent(balance);

        String mailTitle = "XENDIT账户余额(Do-It)";

        mailUtil.sendHtmlMail(content,receivers,mailTitle);
        String mobiles = sysParamService.getSysParamValue(SysParamContants.SMS_RECEIVERS_LOANBALANCE);
        if (mobiles == null || mobiles.equals("")){
            mobiles = "17610156636";
        }

        String smsContont = "XENDIT放款账户余额：" +balance;
        SmsCodeMandaoUtil.sendSmsCode(mobiles,smsContont);

        log.info("查询XENDIT放款账户余额邮件定时发送完成{}",System.currentTimeMillis()-start);
    }
    public String mailContent(BigDecimal balance){

        StringBuffer sb = new StringBuffer();
        Date today = new Date();
        Date chinaDate = DateUtils.addDateWithHour(today,1);
        String todayStr = DateUtils.DateToString2(chinaDate);
        // 邮件前面的说明
        sb.append("<p style='color:#F00'>统计时间截止到中国时间  " + todayStr + "</p>");

        // 各短信平台账户余额
        sb.append("<h3>(Do-It)放款账户余额</h3>");
        // 表1表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\" width=\"400px\">");
        sb.append("<tr>")
                .append("<th>").append("    放款账户    ")
                .append("<th>").append("    余额阈值    ")
                .append("<th>").append("    账户余额   ")
                .append("<th>").append("   是否低于阈值  ")
        ;

        String css = "";
        if (balance.compareTo(BigDecimal.valueOf(200000000)) <=0){
            css = "<td align=\"center\" style='color:#F00'>";
        }else {
            css = "<td align=\"center\">";
        }

        sb.append("<tr>")
                .append("<td align=\"center\">").append("XENDIT")
                .append("<td align=\"center\">").append("2亿rp")
                .append("<td align=\"center\">").append(balance)
                .append(css).append( balance.compareTo(BigDecimal.valueOf(200000000)) <=0 ? "是":"否")
        ;
        sb.append("</table>");
        return sb.toString();
    }

}

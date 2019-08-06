package com.yqg.service;

import com.yqg.common.constants.SysParamContants;
import com.yqg.common.enums.order.PayStatusEnum;
import com.yqg.service.check.OrdPayCheckService;
import com.yqg.service.system.service.SysParamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: caomiaoke
 * Date: 08/02/2018
 * Time: 2:57 PM
 */

@Service
@Slf4j
public class CheckOrdPayService {

    @Autowired
    private SysParamService sysParamService;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private OrdPayCheckService ordPayCheckService;

    @Value("${spring.mail.username}")
    private String sendFrom;


    @Value("${excel.file.dir}")
    private String excelFileDir;

    public void checkOrdPay(){
        log.info("<====== 开始与第三方对账放款成功的订单, start: {} =======>", new Date());
        ordPayCheckService.checkPayOrder(PayStatusEnum.SUCCEED);
        log.info("<====== 结束与第三方对账放款成功的订单, start: {} =======>", new Date());


        log.info("<====== 开始与第三方对账放款失败的订单, start: {} =======>", new Date());
        ordPayCheckService.checkPayOrder(PayStatusEnum.FAILED);
        log.info("<====== 结束与第三方对账放款失败的订单, start: {} =======>", new Date());

        log.info("<====== 开始将对账结果保存到Excel, start: {} =======>", new Date());
        ordPayCheckService.saveToExcel();
        log.info("<====== 结束将对账结果保存到Excel, start: {} =======>", new Date());



        log.info("<====== 开始将对账结果以邮件形式发送, start: {} =======>", new Date());
        try {
            sendOrdPayCheckResults();
        } catch (MessagingException e) {
            log.error("邮件发送异常：date：{}",getNDate(-1),e);
        }
        log.info("<====== 结束将对账结果以邮件形式发送, start: {} =======>", new Date());
    }


    private void sendOrdPayCheckResults() throws MessagingException {
        String dateStr = getNDate(-1);

        String sendToList = sysParamService.getSysParamValue(SysParamContants.MAIL_RECEIVERS_PAY_CHECK);
        if (sendToList == null || sendToList.equals("")){
//            receivers = "wanghuaizhou@yishufu.com,luhong@yishufu.com,caomiaoke@yishufu.com";
            sendToList = "caomiaoke@yishufu.com";
        }

        String[] sendToArray = sendToList.split(",");

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);// 第二个参数设置为true，表示允许添加附件
        helper.setFrom(sendFrom);
        helper.setTo(sendToArray);
        helper.setSubject("Do-It"+dateStr + "放款订单审核");
        helper.setText("附件:\n "+dateStr+"放款订单与第三方二次确认结果.\n状态说明：\n1 = 放款成功，2 = 放款失败，0 = 未找到该订单或查询异常; \n");
        helper.addAttachment("orderPayChecked_"+dateStr+".xls", new File(excelFileDir+"/orderPayChecked_"+dateStr+".xls"));
        javaMailSender.send(mimeMessage);
    }


    private static String getNDate(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, days);
        return new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
    }
}

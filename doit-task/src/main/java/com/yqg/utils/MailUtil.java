package com.yqg.utils;

import com.yqg.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.Address;
import javax.mail.SendFailedException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Didit Dwianto on 2018/1/6.
 */
@Component
@Slf4j
public class MailUtil {

    @Value("${spring.mail.username}")
    private String username;

    @Autowired
    private JavaMailSender mailSender;


    public void sendHtmlMail(String sb, String receivers, String theme) {

        String[] mails = receivers.split(",");
        sendHtmlMail(sb,mails,theme);
    }

    public void sendHtmlMail(String sb, String[] receivers, String theme) {

        MimeMessage message = null;
        JavaMailSender javaMailSender = new JavaMailSenderImpl();
        try {
            message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(username);
            helper.setTo(receivers);
            helper.setSubject(theme);
            helper.setText(sb, true);
            mailSender.send(message);
        } catch (Throwable e) {
            log.error("sendHtmlMail fail.", e);
            String[] invalid = getInvalidAddresses(e);
            log.info("发送前的名单："+Arrays.toString(receivers));
            log.info("异常名单："+Arrays.toString(invalid));
            log.info("");
            if (invalid != null) {
                receivers = filterByArray(receivers,invalid);
                log.info("去除异常名单后："+Arrays.toString(receivers));
                sendHtmlMail(sb,receivers,theme);
            }
        }
    }

    /**
     * 从异常获取无效地址
     * @param e
     * @return
     */
    private static String[] getInvalidAddresses(Throwable e) {
        if (e == null) {
            return null;
        }
        if (e instanceof MailSendException) {
            System.out.println("e instanceof SendFailedException");
            Exception[] exceptions = ((MailSendException) e).getMessageExceptions();
            for (Exception exception : exceptions) {
                if (exception instanceof SendFailedException) {
                    return getStringAddress(((SendFailedException) exception).getInvalidAddresses());
                }
            }
        }
        if (e instanceof SendFailedException) {
            return getStringAddress(((SendFailedException) e).getInvalidAddresses());
        }
        return null;
    }

    /**
     * 将Address[]转成String[]
     * @param address
     * @return
     */
    private static String[] getStringAddress(Address[] address) {
        List<String> invalid = new ArrayList<>();
        for (Address a : address) {
            String aa = ((InternetAddress) a).getAddress();
            if (!StringUtils.isEmpty(aa)) {
                invalid.add(aa);
            }
        }
        return invalid.stream().distinct().toArray(String[]::new);
    }

    /**
     * 过滤数组source，规则为数组元素包含了数组filter中的元素则去除
     *
     * @param source
     * @param filter
     * @return
     */
    private static String[] filterByArray(String[] source, String[] filter) {
        List<String> result = new ArrayList<>();
        for (String s : source) {
            boolean contains = false;
            for (String f : filter) {
                if (s.contains(f)) {
                    contains = true;
                    break;
                }
            }
            if (!contains) {
                result.add(s);
            }
        }
        return result.stream().toArray(String[]::new);
    }

    //发送带附件的邮件(三个附件)
    public void sendThreeAttachmentsMail(String receivers, String theme, FileSystemResource file, FileSystemResource file1, FileSystemResource file2, String title, String title1, String title2) {
        MimeMessage message = null;
        JavaMailSender javaMailSender = new JavaMailSenderImpl();
        String[] mails = receivers.split(",");
        try {
            message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(username);
            helper.setTo(mails);
            helper.setSubject(theme);
            Calendar calendar = Calendar.getInstance();//打印系统当前时间
            calendar.add(Calendar.DATE, -1);//得到前一天
            String yestedayDate = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
            helper.setText("各位，以下是截止到" + yestedayDate + "的相关数据表，请下载附件查看");
            //注意项目路径问题，在调用处设置 读取附件路径
            //添加附件
            helper.addAttachment(title, file);
            helper.addAttachment(title1, file1);
            helper.addAttachment(title2, file2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mailSender.send(message);
    }

    //发送带附件的邮件(一个附件)
    public void sendOneAttachmentsMail(String receivers, String theme, FileSystemResource file, String title) {
        MimeMessage message = null;
        JavaMailSender javaMailSender = new JavaMailSenderImpl();
        String[] mails = receivers.split(",");
        try {
            message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(username);
            helper.setTo(mails);
            helper.setSubject(theme);
            helper.setText("以下是每日外呼号码，请下载附件查看。");
            //注意项目路径问题，在调用处设置 读取附件路径
            //添加附件
            helper.addAttachment(title, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mailSender.send(message);
    }
}

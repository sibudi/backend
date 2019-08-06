package com.yqg.service;

import com.yqg.common.constants.SysParamContants;
import com.yqg.service.scheduling.RiskMailSenderService;
import com.yqg.service.system.service.SysParamService;
import com.yqg.utils.MailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * Created by Didit Dwianto on 2018/1/6.
 */
@Service
@Slf4j
public class RiskMailService {


    @Autowired
    private SysParamService sysParamService;

    @Autowired
    private RiskMailSenderService riskMailSenderService;

    @Autowired
    private MailUtil mailUtil;

    //   Doit 风控每日关键指标邮件
    public void riskMailSender() {
        log.info("风控每日关键指标邮件 定时发送");
        long start = System.currentTimeMillis();
        String receivers = sysParamService.getSysParamValue(SysParamContants.RISK_MAIL_RECEIVERS_KEYINDEX);// risk_Mail_RECEIVERS_keyIndex
        if (receivers == null || receivers.equals("")){
            receivers = "weizhaohuan@yinyichina.cn,liqipeng@yinyichina.cn,zhengqiaoling@yinyichina.cn,yexiaomei@yinyichina.cn,wanghuaizhou@yinyichina.cn,yaoweibin@yinyichina.cn,zhoupeixin@yinyichina.cn,sarah.zhao@yinyichina.cn,huzhenhuan@yinyichina.cn,kadi.kusuma@do-it.id";
        }
        String sb = riskMailSenderService.getDayKeyHtmlContext();
        String theme = "Do-It风控每日关键指标";
        mailUtil.sendHtmlMail(sb,receivers,theme);
        log.info("风控每日关键指标邮件 发送完成{}",System.currentTimeMillis()-start);
    }


    //  Doit 风控每日逾期指标邮件
    public void keyIndexSender() {
        log.info("风控每日逾期指标邮件 发送");
        long start = System.currentTimeMillis();
        String receivers = sysParamService.getSysParamValue(SysParamContants.REMINDER_MAIL_KEYINDEX);//Do-It风控每日关键指标（内部）
        if (receivers == null || receivers.equals("")){
            receivers = "weizhaohuan@yinyichina.cn,liqipeng@yinyichina.cn,yexiaomei@yinyichina.cn,zhengqiaoling@yinyichina.cn,zhangshanshan@yinyichina.cn,zhoupeixin@yinyichina.cn,wanghuaizhou@yinyichina.cn,chenpeng@yinyichina.cn,wangjian@yinyichina.cn,liquanxia@yinyichina.cn,jc.renyu@gmail.com,sarah.zhao@yinyichina.cn,kadi.kusuma@do-it.id,kadi.kusuma@do-it.id,huzhenhuan@yinyichina.cn";
        }

        String sb = riskMailSenderService.getOverDuaHtmlContext();
        String theme = "Do-It风控每日逾期指标";
        mailUtil.sendHtmlMail(sb,receivers,theme);
        log.info("风控每日逾期指标邮件 发送完成{}",System.currentTimeMillis()-start);
    }


//    //  Doit 审核时报
//    public void chackMailSender() {
//        log.info("Doit审核时报");
//        long start = System.currentTimeMillis();
//        String receivers = sysParamService.getSysParamValue(SysParamContants.REMINDER_AND_CHECK);// Do-It风控每日关键指标（内部）
//        if (receivers == null || receivers.equals("")) {
//            receivers = "yexiaomei@yinyichina.cn,zhengqiaoling@yinyichina.cn,zhangshanshan@yinyichina.cn,zhoupeixin@yinyichina.cn,yaoweibin@yinyichina.cn,wanghuaizhou@yinyichina.cn,huzhenhuan@yinyichina.cn";
//        }
//        String sb = riskMailSenderService.chackMailSender();
//        String theme = "Doit审核时报";
//        mailUtil.sendHtmlMail(sb,receivers,theme);
//        log.info("Doit审核时报 {}",System.currentTimeMillis()-start);
//    }

    //  Doit 风控内部邮件
    public void riskInMailSender() {
        log.info("Doit风控内部邮件");
        long start = System.currentTimeMillis();
        String receivers = sysParamService.getSysParamValue(SysParamContants.REMINDER_MAIL_RISKINMAIL);// Do-It风控每日关键指标（内部）
        if (receivers == null || receivers.equals("")){
            receivers = "weizhaohuan@yinyichina.cn,liqipeng@yinyichina.cn,yexiaomei@yinyichina.cn,zhengqiaoling@yinyichina.cn,zhangshanshan@yinyichina.cn,zhoupeixin@yinyichina.cn,wanghuaizhou@yinyichina.cn,sarah.zhao@yinyichina.cn";
        }
        try{
            String sb = riskMailSenderService.riskInMailSender();
            String theme = "Doit风控内部邮件";
            mailUtil.sendHtmlMail(sb,receivers,theme);
        }catch (Exception e){
            log.error("获取报表异常",e);
        }

        log.info("Doit风控内部邮件 {}",System.currentTimeMillis()-start);
    }

    //  Doit 风控外呼邮件
    public void riskInMailSenderWithCall() {
        log.info("Doit风控外呼邮件");
        long start = System.currentTimeMillis();
        String receivers = sysParamService.getSysParamValue(SysParamContants.REMINDER_MAIL_CALLOUT_MAIL);// Do-It风控每日关键指标（内部）
        if (receivers == null || receivers.equals("")){
            receivers = "weizhaohuan@yinyichina.cn,liqipeng@yinyichina.cn,yexiaomei@yinyichina.cn,zhengqiaoling@yinyichina.cn,zhangshanshan@yinyichina.cn,zhoupeixin@yinyichina.cn,wanghuaizhou@yinyichina.cn,sarah.zhao@yinyichina.cn";
        }

        String sb = riskMailSenderService.riskInMailSenderWithCall();
        String theme = "Doit风控外呼邮件";
        mailUtil.sendHtmlMail(sb,receivers,theme);
        log.info("Doit风控外呼邮件 {}",System.currentTimeMillis()-start);
    }

    //   Doit 新老用户每日逾期率
    public void doitDayOverdueRate() throws Exception{
        log.info(" Doit新老用户每日逾期率");
        long start = System.currentTimeMillis();
        String receivers = sysParamService.getSysParamValue(SysParamContants.DAY_OVERDUE_RATE);// Do-It风控每日关键指标（内部）
        if (receivers == null || receivers.equals("")){
            receivers = "yexiaomei@yinyichina.cn,wanghuaizhou@yinyichina.cn,chenpeng@yinyichina.cn,liquanxia@yinyichina.cn,wangjian@yinyichina.cn,yaoweibin@yinyichina.cn,huzhenhuan@yinyichina.cn";
        }
        //注意项目路径问题，读取附件的路径 以及附件命名
        String dir = riskMailSenderService.doitDayOverdueRate();
        String dir1 = riskMailSenderService.doitDayOverdueRateWithNewUser();
        String dir2 = riskMailSenderService.doitDayOverdueRateWithOldUser();

        String title = "每日逾期率.xls";
        String title1 = "每日逾期率(新户).xls";
        String title2 = "每日逾期率(老户).xls";

        FileSystemResource file = new FileSystemResource(new File(dir));
        FileSystemResource file1 = new FileSystemResource(new File(dir1));
        FileSystemResource file2 = new FileSystemResource(new File(dir2));

        String theme = "Do-it新老用户每日逾期率";
        mailUtil.sendThreeAttachmentsMail(receivers,theme,file,file1,file2,title,title1,title2);

        log.info(" Doit新老用户每日逾期率 {}",System.currentTimeMillis()-start);
    }

    //   Doit 运营每日外呼号码
    public void doitDayCallNumber() throws Exception{
        log.info("Doit运营每日外呼号码");
        long start = System.currentTimeMillis();

        String receivers = "chenpeng@yinyichina.cn,liquanxia@yinyichina.cn,wangjian@yinyichina.cn";
//        String receivers = "wanghuaizhou@yinyichina.cn";

        //注意项目路径问题，读取附件的路径 以及附件命名
        String dir = riskMailSenderService.doitCallNumber();

        String title = "运营每日外呼号码.xls";

        FileSystemResource file = new FileSystemResource(new File(dir));

        String theme = "Do-it运营每日外呼号码";
        mailUtil.sendOneAttachmentsMail(receivers,theme,file,title);

        log.info(" Doit 运营每日外呼号码 {}",System.currentTimeMillis()-start);
    }


    //  Doit 审核人员每日审核状况 邮件
    public void doitDayReviewSender() {
        log.info("审核人员每日审核状况邮件 发送");
        long start = System.currentTimeMillis();
        String receivers = sysParamService.getSysParamValue(SysParamContants.REVIEWER_DAY);// 审核人员每日审核状况
        log.info("审核人员每日审核状况邮件 发送名单"+receivers);
        if (receivers == null || receivers.equals("")){
            receivers = "zhengqiaoling@yinyichina.cn,zhangshanshan@yinyichina.cn,zhoupeixin@yinyichina.cn,wanghuaizhou@yinyichina.cn,jonathan@do-it.id,airlangga@do-it.id,dolly.hasbiar@do-it.id";
        }

        String sb = riskMailSenderService.doitDayReview();
        String theme = "Do-It daily check by auditor";
        mailUtil.sendHtmlMail(sb,receivers,theme);
        log.info("审核人员每日审核状况邮件 发送完成{}",System.currentTimeMillis()-start);
    }


    //  Doit 初审/复审员对应其通过率和D8逾期率与平均逾期率差值表
    public void doitDayReviewWeek() {
        log.info("初/复审人员通过率-D8逾期率差表格 发送");
        long start = System.currentTimeMillis();
        String receivers = sysParamService.getSysParamValue(SysParamContants.REVIEWER_DAY);// 审核人员每日审核状况
        if (receivers == null || receivers.equals("")){
            receivers = "zhengqiaoling@yinyichina.cn,zhangshanshan@yinyichina.cn,zhoupeixin@yinyichina.cn,wanghuaizhou@yinyichina.cn,jonathan@do-it.id,airlangga@do-it.id";
        }

        String sb = riskMailSenderService.doitDayReviewWeek();
        String theme = "Do-It FC/SC pass rate - D8 difference overdue rate per auditor";
        mailUtil.sendHtmlMail(sb,receivers,theme);
        log.info("初/复审人员通过率-D8逾期率差表格 发送完成{}",System.currentTimeMillis()-start);
    }


    // Do-It 运营日报
    public void doitDayOperationMail() {
        log.info("Do-It 运营日报 发送");
        long start = System.currentTimeMillis();
        String receivers = sysParamService.getSysParamValue(SysParamContants.OPERATION_DAY);// 运营日报发送名单
        if (receivers == null || receivers.equals("")){
            receivers = "yexiaomei@yinyichina.cn,wanghuaizhou@yinyichina.cn,yaoweibin@yinyichina.cn,huzhenhuan@yinyichina.cn,wangjian@yinyichina.cn,liangdezhao@yinyichina.cn";
        }

        String sb = riskMailSenderService.doitDayOperation();
        String theme = "Do-It 运营日报";
        mailUtil.sendHtmlMail(sb,receivers,theme);
        log.info("Do-It 运营日报 发送完成{}",System.currentTimeMillis()-start);
    }


    // Do-It 催收时报
    public void doitCollectionMail() {
        log.info("Do-It 催收时报 发送");
        long start = System.currentTimeMillis();
        String receivers = sysParamService.getSysParamValue(SysParamContants.COLLECTION_HOUR);// 运营日报发送名单
        log.info("催收时报发送名单"+receivers);
        if (receivers == null || receivers.equals("")){
            receivers = "yexiaomei@yinyichina.cn,wanghuaizhou@yinyichina.cn,yaoweibin@yinyichina.cn,huzhenhuan@yinyichina.cn,wangjian@yinyichina.cn,liquanxia@yinyichina.cn,chenpeng@yinyichina.cn";
        }

        String sb = riskMailSenderService.doitCollectionMail();
        String theme = "Do-It 催收时报";
        mailUtil.sendHtmlMail(sb,receivers,theme);
        log.info("Do-It 催收时报 发送完成{}",System.currentTimeMillis()-start);
    }


    // Do-It 催收质检  QI: Quality inspection
    public void doitCollectionQIMail() {
        log.info("Do-It 催收质检邮件 发送");
        long start = System.currentTimeMillis();
        String receivers = sysParamService.getSysParamValue(SysParamContants.COLLECTION_HOUR_QI);// 运营日报发送名单
        if (receivers == null || receivers.equals("")){
            receivers = "yexiaomei@yinyichina.cn,yaoweibin@yinyichina.cn,huzhenhuan@yinyichina.cn,wangjian@yinyichina.cn,liquanxia@yinyichina.cn,chenpeng@yinyichina.cn ,mike.electa@do-it.id,kadi.kusuma@do-it.id,jc@do-it.id,rinawaty.purba16@gmail.com";
        }
        String sb = riskMailSenderService.doitCollectionQIMail();
        String theme = "Do-It 催收质检";
        mailUtil.sendHtmlMail(sb,receivers,theme);
        log.info("Do-It 催收质检 发送完成{}",System.currentTimeMillis()-start);
    }

}

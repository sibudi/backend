package com.yqg.service.scheduling;

import com.yqg.common.excel.ExcelData;
import com.yqg.common.excel.ExcelDataType;
import com.yqg.common.excel.ExcelUtil;
import com.yqg.common.excel.ExecelFileType;
import com.yqg.common.utils.DESUtils;
import com.yqg.common.utils.DateUtils;
import com.yqg.management.dao.CollectionOrderHistoryDao;
import com.yqg.management.entity.*;
import com.yqg.order.dao.OrdBlackDao;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.entity.*;
import com.yqg.order.entity.DayLoanCount2;
import com.yqg.risk.dao.ReminderAndCheckDao;
import com.yqg.risk.entity.*;
import com.yqg.risk.entity.DayLoanAmout;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Didit Dwianto on 2018/1/6.
 */
@Service
@Slf4j
public class RiskMailSenderService {


    @Autowired
    private OrdBlackDao ordBlackDao;

    @Autowired
    private OrdDao ordDao;

    @Autowired
    private ReminderAndCheckDao reminderAndCheckDao;

    @Autowired
    private ExcelUtil excelUtil;

    @Autowired
    private RiskDataSynService riskDataSynService;

    @Autowired
    private CollectionOrderHistoryDao collectionOrderHistoryDao;

    /**
     * Do-It 风控每日关键指标
     */
    public String getDayKeyHtmlContext() {
        StringBuffer sb = new StringBuffer();
        Date today = new Date();
        Date chinaDate = DateUtils.addDateWithHour(today, 1);
        String todayStr = DateUtils.DateToString2(chinaDate);
        // 邮件前面的说明
        sb.append("<p style='color:#F00'>请注意保密</p>")
                .append("<p style='color:#F00'>统计时间截止到中国时间" + todayStr + "</p>")
                .append("<p style='text-align:right'>统计数据来自--张闪闪</p>");


//        // 表1标题：各环节当天累计订单数（新户+老户）
//        sb.append("<h3>1、各环节当天累计订单数（新户+老户）</h3>");
//        // 表1表格体
//        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
//        sb.append("<tr>")
//                .append("<th>").append("日期")
//                .append("<th>").append("申请订单")
//                .append("<th>").append("提交订单")
//                .append("<th>").append("机审通过")
//                .append("<th>").append("初审通过")
//                .append("<th>").append("复审通过")
//                .append("<th>").append("放款")
//                .append("<th>").append("放款成功")
//                .append("<th>").append("机审不通过")
//                .append("<th>").append("初审不通过")
//                .append("<th>").append("复审不通过")
//        ;
//
//        List<PassLinksNum> passLinksNumList = ordBlackDao.getPassLinksNum();
//        for (PassLinksNum info : passLinksNumList) {
//            sb.append("<tr>")
//                    .append("<td align=\"center\">").append(info.getDate())
//                    .append("<td align=\"center\">").append(info.getApplyingNum())
//                    .append("<td align=\"center\">").append(info.getCommitNum())
//                    .append("<td align=\"center\">").append(info.getAutoCheckPassNum())
//                    .append("<td align=\"center\">").append(info.getFirstCheckPassNum())
//                    .append("<td align=\"center\">").append(info.getSecondCheckPassNum())
//                    .append("<td align=\"center\">").append(info.getLendNum())
//                    .append("<td align=\"center\">").append(info.getLendSuccessNum())
//                    .append("<td align=\"center\">").append(info.getAutoCheckNoPassNum())
//                    .append("<td align=\"center\">").append(info.getFirstCheckNoPassNum())
//                    .append("<td align=\"center\">").append(info.getSecondCheckNoPassNum())
//            ;
//        }
//        sb.append("</table>");
//
//        // 表2标题：各环节当天累计订单数（新户）
//        sb.append("<h3>2、各环节当天累计订单数（新户）</h3>");
//        // 表2表格体
//        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
//        sb.append("<tr>")
//                .append("<th>").append("日期")
//                .append("<th>").append("申请订单")
//                .append("<th>").append("提交订单")
//                .append("<th>").append("机审通过")
//                .append("<th>").append("初审通过")
//                .append("<th>").append("复审通过")
//                .append("<th>").append("放款")
//                .append("<th>").append("放款成功")
//                .append("<th>").append("机审不通过")
//                .append("<th>").append("初审不通过")
//                .append("<th>").append("复审不通过")
//        ;
//
//        List<PassLinksNum> passLinksNumListWithNewUser = ordBlackDao.getPassLinksNumWithNewUser();
//        for (PassLinksNum info : passLinksNumListWithNewUser) {
//            sb.append("<tr>")
//                    .append("<td align=\"center\">").append(info.getDate())
//                    .append("<td align=\"center\">").append(info.getApplyingNum())
//                    .append("<td align=\"center\">").append(info.getCommitNum())
//                    .append("<td align=\"center\">").append(info.getAutoCheckPassNum())
//                    .append("<td align=\"center\">").append(info.getFirstCheckPassNum())
//                    .append("<td align=\"center\">").append(info.getSecondCheckPassNum())
//                    .append("<td align=\"center\">").append(info.getLendNum())
//                    .append("<td align=\"center\">").append(info.getLendSuccessNum())
//                    .append("<td align=\"center\">").append(info.getAutoCheckNoPassNum())
//                    .append("<td align=\"center\">").append(info.getFirstCheckNoPassNum())
//                    .append("<td align=\"center\">").append(info.getSecondCheckNoPassNum())
//            ;
//        }
//        sb.append("</table>");
//
//
//        // 表3标题： 各环节当天申请订单的当前通过率（新户+老户）
//        sb.append("<h3>3、各环节当天申请订单的当前通过率（新户+老户）</h3>");
//        // 表3表格体
//        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
//        sb.append("<tr>")
//                .append("<th>").append("申请日期")
//                .append("<th>").append("申请笔数")
//                .append("<th>").append("提交笔数")
//                .append("<th>").append("通过笔数")
//                .append("<th>").append("放款笔数")
//                .append("<th>").append("申请提交率")
//                .append("<th>").append("提交通过率")
//                .append("<th>").append("申请通过率")
//        ;
//        List<PassApplyLinksNum> passApplyLinksNum = ordBlackDao.getPassApplyLinksNum();
//        for (PassApplyLinksNum info : passApplyLinksNum) {
//            sb.append("<tr>")
//                    .append("<td align=\"center\">").append(info.getP1())
//                    .append("<td align=\"center\">").append(info.getP2())
//                    .append("<td align=\"center\">").append(info.getP3())
//                    .append("<td align=\"center\">").append(info.getP4())
//                    .append("<td align=\"center\">").append(info.getP5())
//                    .append("<td align=\"center\">").append(info.getP6())
//                    .append("<td align=\"center\">").append(info.getP7())
//                    .append("<td align=\"center\">").append(info.getP8())
//            ;
//        }
//        sb.append("</table>");
//
//        // 表4标题：各环节当天申请订单的当前通过笔数（新户）
//        sb.append("<h3>4、各环节当天申请订单的当前通过笔数（新户）</h3>");
//        // 表4表格体
//        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
//        sb.append("<tr>")
//                .append("<th>").append("申请日期")
//                .append("<th>").append("申请订单")
//                .append("<th>").append("提交订单")
//                .append("<th>").append("机审通过")
//                .append("<th>").append("初审通过")
//                .append("<th>").append("复审通过")
//                .append("<th>").append("放款")
//                .append("<th>").append("放款成功")
//                .append("<th>").append("机审不通过")
//                .append("<th>").append("初审不通过")
//                .append("<th>").append("初审手动取消")
//                .append("<th>").append("复审不通过")
//        ;
//        List<PassLinksRate> passLinksRateList = ordBlackDao.getPassLinksRate();
//        for (PassLinksRate info : passLinksRateList) {
//            sb.append("<tr>")
//                    .append("<td align=\"center\">").append(info.getDate())
//                    .append("<td align=\"center\">").append(info.getApplyingNum())
//                    .append("<td align=\"center\">").append(info.getCommitNum())
//                    .append("<td align=\"center\">").append(info.getAutoCheckPassNum())
//                    .append("<td align=\"center\">").append(info.getFirstCheckPassNum())
//                    .append("<td align=\"center\">").append(info.getSecondCheckPassNum())
//                    .append("<td align=\"center\">").append(info.getLendNum())
//                    .append("<td align=\"center\">").append(info.getLendSuccessNum())
//                    .append("<td align=\"center\">").append(info.getAutoCheckNoPassNum())
//                    .append("<td align=\"center\">").append(info.getFirstCheckNoPassNum())
//                    .append("<td align=\"center\">").append(info.getFirstCheckhandNoPassNum())
//                    .append("<td align=\"center\">").append(info.getSecondCheckNoPassNum())
//            ;
//        }
//        sb.append("</table>");
//
//        // 表5标题：各环节当天申请订单的当前通过率（新户）
//        sb.append("<h3>5、各环节当天申请订单的当前通过率（新户）</h3>");
//        // 表5表格体
//        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
//        sb.append("<tr>")
//                .append("<th>").append("申请日期")
//                .append("<th>").append("申请提交率")
//                .append("<th>").append("机审通过率")
//                .append("<th>").append("初审通过率")
//                .append("<th>").append("复审通过率")
//                .append("<th>").append("申请通过率")
//                .append("<th>").append("放款成功率")
//                .append("<th>").append("机审+人工审核通过率 ")
//        ;
//        List<PassLinksRateOld> rate6 = ordBlackDao.getPassLinksRateOld();
//        for (PassLinksRateOld info : rate6) {
//            sb.append("<tr>")
//                    .append("<td align=\"center\">").append(info.getDate())
//                    .append("<td align=\"center\">").append(info.getCommitRate())
//                    .append("<td align=\"center\">").append(info.getAutoCheckPassRate())
//                    .append("<td align=\"center\">").append(info.getFirstCheckPassRate())
//                    .append("<td align=\"center\">").append(info.getSecondCheckPassRate())
//                    .append("<td align=\"center\">").append(info.getApplyPassRate())
//                    .append("<td align=\"center\">").append(info.getLendSuccessRate())
//                    .append("<td align=\"center\">").append(info.getApplyAllRate())
//            ;
//        }
//        sb.append("</table>");
//
//        // 表6标题：各环节当天申请订单的当前通过率（老户）
//        sb.append("<h3>6、各环节当天申请订单的当前通过率（老户）</h3>");
//        // 表6表格体
//        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
//        sb.append("<tr>")
//                .append("<th>").append("申请日期")
//                .append("<th>").append("申请笔数")
//                .append("<th>").append("提交笔数")
//                .append("<th>").append("通过笔数")
//                .append("<th>").append("放款笔数")
//                .append("<th>").append("申请提交率")
//                .append("<th>").append("提交通过率（机审）")
//                .append("<th>").append("申请通过率")
//                .append("<th>").append("放款成功率")
//        ;
//        List<TodayApplyOrderRate> rate7 = ordBlackDao.getTodayApplyOrderRate();
//        for (TodayApplyOrderRate info : rate7) {
//            sb.append("<tr>")
//                    .append("<td align=\"center\">").append(info.getDate())
//                    .append("<td align=\"center\">").append(info.getApplyingNum())
//                    .append("<td align=\"center\">").append(info.getCommitNum())
//                    .append("<td align=\"center\">").append(info.getAutoCheckPassNum())
//                    .append("<td align=\"center\">").append(info.getLendNum())
//                    .append("<td align=\"center\">").append(info.getCommitRate())
//                    .append("<td align=\"center\">").append(info.getApplyAllRate())
//                    .append("<td align=\"center\">").append(info.getApplyPassRate())
//                    .append("<td align=\"center\">").append(info.getLendSuccessRate())
//            ;
//        }
//        sb.append("</table>");

        // 表1标题：每日放款金额_新老合计 （单位：万/RMB）
        sb.append("<h3>1、每日放款金额_新老合计 （单位：万/RMB）</h3>");
        // 表1表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("放款日期")
                .append("<th>").append("合计金额")
                .append("<th>").append("新户合计")
                .append("<th>").append("老户合计")
                .append("<th>").append("老户占比")
                .append("<th>").append("新户占比")
        ;

        List<com.yqg.order.entity.DayLoanAmout> dayLoanAmoutList = ordDao.getDayLoanAmout();
        for (com.yqg.order.entity.DayLoanAmout info : dayLoanAmoutList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getLendDate())
                    .append("<td align=\"center\">").append(info.getTotalAmount())
                    .append("<td align=\"center\">").append(info.getNewTotal())
                    .append("<td align=\"center\">").append(info.getOldTotal())
                    .append("<td align=\"center\">").append(info.getOldProportion())
                    .append("<td align=\"center\">").append(info.getNewProportion())
            ;
        }
        sb.append("</table>");

        // 表2标题：每日放款笔数_新老合计
        sb.append("<h3>2、每日放款笔数_新老合计 </h3>");
        // 表2表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("日期")
                .append("<th>").append("合计")
                .append("<th>").append("新户合计")
                .append("<th>").append("老户合计")
                .append("<th>").append("新户占比")
                .append("<th>").append("老户占比")
        ;

        List<DayLoanCount2> dayLoanCList = ordDao.getDayLoanCount();
        for (DayLoanCount2 info : dayLoanCList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getLendDate())
                    .append("<td align=\"center\">").append(info.getTotalLendNum())
                    .append("<td align=\"center\">").append(info.getNewLendNum())
                    .append("<td align=\"center\">").append(info.getOldLendNum())
                    .append("<td align=\"center\">").append(info.getNewProportion())
                    .append("<td align=\"center\">").append(info.getOldProportion())
            ;
        }
        sb.append("</table>");

        // 表3标题：各环节当天申请订单的当前通过笔数（新户600产品）
        sb.append("<h3>3、各环节当天申请订单的当前通过笔数（新户600产品） </h3>");
        // 表3表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("申请日期")
                .append("<th>").append("申请订单")
                .append("<th>").append("提交订单")
                .append("<th>").append("机审通过")
                .append("<th>").append("进入初审")
                .append("<th>").append("初审完成")
                .append("<th>").append("初审完成本人外呼")
                .append("<th>").append("进入复审")
                .append("<th>").append("复审完成")
                .append("<th>").append("复审通过")
                .append("<th>").append("风控通过")
                .append("<th>").append("多头黑名单拒绝")
                .append("<th>").append("风控and黑名单通过")
                .append("<th>").append("待放款")
                .append("<th>").append("放款成功")
        ;

        List<PassOrderCountWithNewUserAnd600Product> passOrderCountWithNewUserAnd600ProductList = ordDao.getPassOrderCountWithNewUserAnd600Product();
        for (PassOrderCountWithNewUserAnd600Product info : passOrderCountWithNewUserAnd600ProductList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getApplyDate())
                    .append("<td align=\"center\">").append(info.getApplyNum())
                    .append("<td align=\"center\">").append(info.getCommmitNum())
                    .append("<td align=\"center\">").append(info.getAutoCheckPass())
                    .append("<td align=\"center\">").append(info.getIntoFC())
                    .append("<td align=\"center\">").append(info.getFCfinish())
                    .append("<td align=\"center\">").append(info.getFCPassOutCall())
                    .append("<td align=\"center\">").append(info.getIntoSC())
                    .append("<td align=\"center\">").append(info.getSCfinish())
                    .append("<td align=\"center\">").append(info.getSCpass())
                    .append("<td align=\"center\">").append(info.getRiskPass())
                    .append("<td align=\"center\">").append(info.getBlackListMultiHeadNum())
                    .append("<td align=\"center\">").append(info.getRiskBlacklistPassNum())
                    .append("<td align=\"center\">").append(info.getLendNum())
                    .append("<td align=\"center\">").append(info.getLendSuccessNum())
            ;
        }
        sb.append("</table>");

        // 表4标题：各环节当天申请订单的当前通过率（新户600产品）
        sb.append("<h3>4、各环节当天申请订单的当前通过率（新户600产品） </h3>");
        // 表4表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("申请日期")
                .append("<th>").append("600申请提交率")
                .append("<th>").append("机审通过率")
                .append("<th>").append("人工初审通过率")
                .append("<th>").append("本人外呼通过率")
                .append("<th>").append("复审通过率")
                .append("<th>").append("风控通过率")
                .append("<th>").append("风控and黑名单通过率")
                .append("<th>").append("免审核占机审通过比例")
                .append("<th>").append("免审核占提交比例")
                .append("<th>").append("多头黑名单拒绝率")
                .append("<th>").append("放款成功率")
        ;

        List<PassOrderRateWithNewUserAnd600Product> passOrderRateWithNewUserAnd600ProductList = ordDao.getPassOrderRateWithNewUserAnd600Product();
        for (PassOrderRateWithNewUserAnd600Product info : passOrderRateWithNewUserAnd600ProductList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getApplyDate())
                    .append("<td align=\"center\">").append(info.getCommitRate())
                    .append("<td align=\"center\">").append(info.getAutoCheckPassRate())
                    .append("<td align=\"center\">").append(info.getManFCPassRate())
                    .append("<td align=\"center\">").append(info.getSelfCallPassRate())
                    .append("<td align=\"center\">").append(info.getSCPassRate())
                    .append("<td align=\"center\">").append(info.getRiskPassRate())
                    .append("<td align=\"center\">").append(info.getRiskandBlackListPassRate())
                    .append("<td align=\"center\">").append(info.getNoMCRateInACpass())
                    .append("<td align=\"center\">").append(info.getNoMCrateInCommit())
                    .append("<td align=\"center\">").append(info.getBlackListMuliHeadRate())
                    .append("<td align=\"center\">").append(info.getLendSuccessRate())
            ;
        }
        sb.append("</table>");

        // 表5标题：各环节当天申请订单的当前通过率（新户150产品）
        sb.append("<h3>5、各环节当天申请订单的当前通过率（新户150产品） </h3>");
        // 表5表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("提交日期")
                .append("<th>").append("600到150")
                .append("<th>").append("600复审拒绝到150")
                .append("<th>").append("进入150")
                .append("<th>").append("审核通过")
                .append("<th>").append("用户确认")
                .append("<th>").append("待放款")
                .append("<th>").append("放款成功")
                .append("<th>").append("审核通过率")
                .append("<th>").append("用户确认比例")
                .append("<th>").append("放款成功率")
        ;

        List<PassOrderRateWithNewUserAnd150Product> passOrderRateWithNewUserAnd150ProductList = ordDao.getPassOrderRateWithNewUserAnd150Product();
        for (PassOrderRateWithNewUserAnd150Product info : passOrderRateWithNewUserAnd150ProductList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getCommitDate())
                    .append("<td align=\"center\">").append(info.getProduct600To150())
                    .append("<td align=\"center\">").append(info.getProduct600SCTo150())
                    .append("<td align=\"center\">").append(info.getGetIntoProduct150())
                    .append("<td align=\"center\">").append(info.getRiskPassNum())
                    .append("<td align=\"center\">").append(info.getCSCommitNum())
                    .append("<td align=\"center\">").append(info.getLendNum())
                    .append("<td align=\"center\">").append(info.getLendSuccessNum())
                    .append("<td align=\"center\">").append(info.getRiskPassRate())
                    .append("<td align=\"center\">").append(info.getCSCommitRate())
                    .append("<td align=\"center\">").append(info.getLendSuccessRate())
            ;
        }
        sb.append("</table>");

        // 表6标题：各环节当天申请订单的当前通过率（新户80产品）
        sb.append("<h3>6、各环节当天申请订单的当前通过率（新户80产品） </h3>");
        // 表6表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("提交日期")
                .append("<th>").append("150到80")
                .append("<th>").append("审核通过")
                .append("<th>").append("用户确认")
                .append("<th>").append("待放款")
                .append("<th>").append("放款成功")
                .append("<th>").append("机审通过率")
                .append("<th>").append("用户确认比例")
                .append("<th>").append("放款成功率")
        ;

        List<PassOrderRateWithNewUserAnd80Product> passOrderRateWithNewUserAnd50ProductList = ordDao.getPassOrderRateWithNewUserAnd80Product();
        for (PassOrderRateWithNewUserAnd80Product info : passOrderRateWithNewUserAnd50ProductList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getCommitDate())
                    .append("<td align=\"center\">").append(info.getGetInProduct80())
                    .append("<td align=\"center\">").append(info.getRiskPassNum())
                    .append("<td align=\"center\">").append(info.getCScommitNum())
                    .append("<td align=\"center\">").append(info.getLendNum())
                    .append("<td align=\"center\">").append(info.getLendSuccessNum())
                    .append("<td align=\"center\">").append(info.getAutoCheckPassRate())
                    .append("<td align=\"center\">").append(info.getCstCommitRate())
                    .append("<td align=\"center\">").append(info.getLendSucRate())
            ;
        }
        sb.append("</table>");

        // 表7标题：各环节当天申请订单的当前通过率（老户）
        sb.append("<h3>7、各环节当天申请订单的当前通过率（老户） </h3>");
        // 表7表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("申请日期")
                .append("<th>").append("老户申请数")
                .append("<th>").append("提交数")
                .append("<th>").append("审核通过")
                .append("<th>").append("待放款")
                .append("<th>").append("放款成功")
                .append("<th>").append("申请提交率")
                .append("<th>").append("审核通过率")
                .append("<th>").append("放款成功率")
        ;

        List<PassOrderRateWithOldUser> passOrderRateWithOldUserList = ordDao.getPassOrderRateWithOldUser();
        for (PassOrderRateWithOldUser info : passOrderRateWithOldUserList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getApplyDate())
                    .append("<td align=\"center\">").append(info.getOldApplyNum())
                    .append("<td align=\"center\">").append(info.getCommitNum())
                    .append("<td align=\"center\">").append(info.getRiskPassNum())
                    .append("<td align=\"center\">").append(info.getLendNum())
                    .append("<td align=\"center\">").append(info.getLendSuccessNum())
                    .append("<td align=\"center\">").append(info.getCommitRate())
                    .append("<td align=\"center\">").append(info.getRiskPassRate())
                    .append("<td align=\"center\">").append(info.getLendSuccessRate())
            ;
        }
        sb.append("</table>");

        // 表8标题： 各环节当天累计订单数（新户600产品）
        sb.append("<h3>8、各环节当天累计订单数（新户600产品） </h3>");
        // 表8表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("申请日期")
                .append("<th>").append("申请")
                .append("<th>").append("提交")
                .append("<th>").append("机审通过")
                .append("<th>").append("待初审")
                .append("<th>").append("待复审")
                .append("<th>").append("待放款")
                .append("<th>").append("放款成功")
                .append("<th>").append("机审不通过")
                .append("<th>").append("初审不通过")
                .append("<th>").append("复审不通过")
        ;

        List<PassOrderCountWithNewUserAnd600Product2> passOrderCountWithNewUserAnd600Product2List = ordDao.getPassOrderCountWithNewUserAnd600Product2();
        for (PassOrderCountWithNewUserAnd600Product2 info : passOrderCountWithNewUserAnd600Product2List) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getDate())
                    .append("<td align=\"center\">").append(info.getApplyNum())
                    .append("<td align=\"center\">").append(info.getCommitNum())
                    .append("<td align=\"center\">").append(info.getAutoCheckPassNum())
                    .append("<td align=\"center\">").append(info.getWaitforFC())
                    .append("<td align=\"center\">").append(info.getWaitforSC())
                    .append("<td align=\"center\">").append(info.getLendNum())
                    .append("<td align=\"center\">").append(info.getLendSuccessNum())
                    .append("<td align=\"center\">").append(info.getAutoCheckFailedNum())
                    .append("<td align=\"center\">").append(info.getFCFailedNum())
                    .append("<td align=\"center\">").append(info.getSCFailedNum())
            ;
        }
        sb.append("</table>");

        // 表9标题： 各环节当天累计订单数（新户150产品）
        sb.append("<h3>9、各环节当天累计订单数（新户150产品） </h3>");
        // 表9表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("拒绝日期")
                .append("<th>").append("进入150")
                .append("<th>").append("审核通过")
                .append("<th>").append("已确认待放款")
                .append("<th>").append("放款成功")
        ;

        List<PassOrderCountWithNewUserAnd150Product> passOrderCountWithNewUserAnd150ProductList = ordDao.getPassOrderCountWithNewUserAnd150Product();
        for (PassOrderCountWithNewUserAnd150Product info : passOrderCountWithNewUserAnd150ProductList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getDate())
                    .append("<td align=\"center\">").append(info.getGetIntoProduct150())
                    .append("<td align=\"center\">").append(info.getAutoCheckoutCallPassNum())
                    .append("<td align=\"center\">").append(info.getCstCommitLendNum())
                    .append("<td align=\"center\">").append(info.getLendSucNum())
            ;
        }
        sb.append("</table>");

        // 表10标题： 各环节当天累计订单数（新户80产品）
        sb.append("<h3>10、各环节当天累计订单数（新户80产品） </h3>");
        // 表10表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("拒绝日期")
                .append("<th>").append("150到80")
                .append("<th>").append("机审通过")
                .append("<th>").append("已确认待放款")
                .append("<th>").append("放款成功")
        ;

        List<PassOrderCountWithNewUserAnd80Product> passOrderCountWithNewUserAnd80ProductList = ordDao.getPassOrderCountWithNewUserAnd80Product();
        for (PassOrderCountWithNewUserAnd80Product info : passOrderCountWithNewUserAnd80ProductList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getDate())
                    .append("<td align=\"center\">").append(info.getGetIntoProduct80())
                    .append("<td align=\"center\">").append(info.getAutoCheckoutCallPassNum())
                    .append("<td align=\"center\">").append(info.getCstCommitLendNum())
                    .append("<td align=\"center\">").append(info.getLendSuccessNum())
            ;
        }
        sb.append("</table>");

        // 表11标题： 各环节当天累计订单数（老户）
        sb.append("<h3>11、各环节当天累计订单数（老户） </h3>");
        // 表11表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("日期")
                .append("<th>").append("老户申请数")
                .append("<th>").append("提交数")
                .append("<th>").append("待放款")
                .append("<th>").append("放款成功")
        ;

        List<OrderCountWithOldUser> orderCountWithOldUserList = ordDao.getOrderCountWithOldUser();
        for (OrderCountWithOldUser info : orderCountWithOldUserList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getDate())
                    .append("<td align=\"center\">").append(info.getApplyNum())
                    .append("<td align=\"center\">").append(info.getCommitNum())
                    .append("<td align=\"center\">").append(info.getLendNum())
                    .append("<td align=\"center\">").append(info.getLendSuccessNum())
            ;
        }
        sb.append("</table>");

        // 表12、注册用户当日页面通过率
        sb.append("<h3>12、注册用户当日页面通过率</h3>");
        // 表12表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("日期")
                .append("<th>").append("注册数")
                .append("<th>").append("注册申请率")
                .append("<th>").append("选择角色通过率")
                .append("<th>").append("填写身份信息通过率")
                .append("<th>").append("基本信息通过率")
                .append("<th>").append("工作或学校信息通过率")
                .append("<th>").append("紧急联系人信息通过率")
                .append("<th>").append("验证信息通过率")
                .append("<th>").append("银行卡通过率")
                .append("<th>").append("额外信息填写率")
        ;
        List<PassLinksAllRate> passLinksAllRateList = ordBlackDao.getPassLinksAllRate();
        for (PassLinksAllRate info : passLinksAllRateList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getCreateDay())
                    .append("<td align=\"center\">").append(info.getRegisterNum())
                    .append("<td align=\"center\">").append(info.getApplyRate())
                    .append("<td align=\"center\">").append(info.getRoleRate())
                    .append("<td align=\"center\">").append(info.getIdentityRate())
                    .append("<td align=\"center\">").append(info.getInformationRate())
                    .append("<td align=\"center\">").append(info.getWorkRate())
                    .append("<td align=\"center\">").append(info.getContactsRate())
                    .append("<td align=\"center\">").append(info.getVerificateRate())
                    .append("<td align=\"center\">").append(info.getBankRate())
                    .append("<td align=\"center\">").append(info.getExtraRate())
            ;
        }
        sb.append("</table>");

        // 表13标题：实名认证每日拒绝率 聚信立 Advance 税卡号
        sb.append("<h3>13、实名认证每日拒绝率</h3>");
        // 表13表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("验证时间")
                .append("<th>").append("IZI未通过人数")
                .append("<th>").append("IZI总人数")
                .append("<th>").append("IZI未通过比例")
                .append("<th>").append("JXL未通过人数")
                .append("<th>").append("JXL总人数")
                .append("<th>").append("JXL未通过率")
                .append("<th>").append("IZI聚信立拒绝率")
                .append("<th>").append("Advance未通过人数")
                .append("<th>").append("Advance总人数")
                .append("<th>").append("Advance未通过率")
                .append("<th>").append("实名认证拒绝率")
        ;
        List<AdvanceVerifyRejectRate> advanceVerifyRejectList = ordBlackDao
                .getAdvanceVerifyRejectList();
        for (AdvanceVerifyRejectRate info : advanceVerifyRejectList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getP1())
                    .append("<td align=\"center\">").append(info.getP2())
                    .append("<td align=\"center\">").append(info.getP3())
                    .append("<td align=\"center\">").append(info.getP4())
                    .append("<td align=\"center\">").append(info.getP5())
                    .append("<td align=\"center\">").append(info.getP6())
                    .append("<td align=\"center\">").append(info.getP7())
                    .append("<td align=\"center\">").append(info.getP8())
                    .append("<td align=\"center\">").append(info.getP9())
                    .append("<td align=\"center\">").append(info.getP10())
                    .append("<td align=\"center\">").append(info.getP11())
                    .append("<td align=\"center\">").append(info.getP12())
            ;
        }
        sb.append("</table>");

        // 表14标题：各渠道今日注册、申请、提交情况
        sb.append("<h3>14、各渠道今日注册、申请、提交情况</h3>");
        // 表14表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("日期")
                .append("<th>").append("渠道")
                .append("<th>").append("注册数")
                .append("<th>").append("申请数")
                .append("<th>").append("提交数")
                .append("<th>").append("放款数")
                .append("<th>").append("注册申请率")
                .append("<th>").append("申请提交率")
                .append("<th>").append("提交放款率")
        ;
        List<ApplyAndSubmitRate> list = ordBlackDao.getApplyAndSubmitRate();
        for (ApplyAndSubmitRate info : list) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getCreateDay())
                    .append("<td align=\"center\">").append(info.getUserSource())
                    .append("<td align=\"center\">").append(info.getRegisterNum())
                    .append("<td align=\"center\">").append(info.getApplyNum())
                    .append("<td align=\"center\">").append(info.getSubmitNum())
                    .append("<td align=\"center\">").append(info.getLengdingNum())
                    .append("<td align=\"center\">").append(info.getApplyRate())
                    .append("<td align=\"center\">").append(info.getSubmitRate())
                    .append("<td align=\"center\">").append(info.getLendingRate())
            ;
        }
        sb.append("</table>");


        // 注释
        sb.append("<br><br>");
        sb.append("<strong style='color:#999966'>注释：</strong>");
        sb.append("<p>申请提交率 = 提交订单人数 / 申请总人数;" +
                "<br>申请通过率 = 复审通过人数 / 申请总人数;" +
                "<br>放款成功率 = 放款成功人数 / 放款人数;" +
                "<br>机审+全部审核通过率 = 复审通过人数 / 提交订单人数;" +
                "</p>")
        ;

        return sb.toString();
    }


    /**
     * Do-It风控每日逾期指标（内部）
     */
    public String getOverDuaHtmlContext() {
        StringBuffer sb = new StringBuffer();

        // 表1标题：新老用户放款笔数与放款金额(RMB:万元)
        sb.append("<h3>1、新老用户放款笔数与放款金额(RMB:万元) Jumlah Nasabah dan Besaran Dana yang Dipinjamkan untuk Nasabah Lama dan Baru ( satuan dalam RMB: Puluhan Ribu)</h3>");
        // 表1表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th height=\"60px\">").append("放款日期 Hari Pencairan")
                .append("<th height=\"60px\">").append("新户放款笔数 Jumlah Nasabah Baru yang Diberi Pinjaman")
                .append("<th height=\"60px\">").append("新户放款金额 Besaran Dana yang Dipinjamkan untuk Nasabah Baru")
                .append("<th height=\"60px\">").append("老户放款笔数 Jumlah Nasabah Lama yang Diberi Pinjaman")
                .append("<th height=\"60px\">").append("老户放款金额 Besaran Dana yang Dipinjamkan untuk Nasabah Lama ")
                .append("<th height=\"60px\">").append("合计放款笔数 Jumlah Nasabah yang Diberi Pinjaman ")
                .append("<th height=\"60px\">").append("合计放款金额 Besaran Dana yang Dipinjamkan")
        ;

        List<NewAndOldLoansAmount> newAndOldLoansAmount = ordDao.getNewAndOldLoansAmount();
        for (NewAndOldLoansAmount info : newAndOldLoansAmount) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getLendDay())
                    .append("<td align=\"center\">").append(info.getNewLendDay())
                    .append("<td align=\"center\">").append(info.getNewLendAmount())
                    .append("<td align=\"center\">").append(info.getOldLendNum())
                    .append("<td align=\"center\">").append(info.getOldLendAmount())
                    .append("<td align=\"center\">").append(info.getAllLendNum())
                    .append("<td align=\"center\">").append(info.getAllLendAmount())
            ;
        }
        sb.append("</table>");

        // 表2标题：金额逾期率
        sb.append("<h3>2、金额逾期率 Jumlah Persentase Keterlambatan</h3>");
        // 表2表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th height=\"60px\">").append("到期日 Tanggal Harus Membayar")
                .append("<th height=\"60px\">").append("应还单量 Jumlah yang Harus Mengembalikan Pinjaman ")
                .append("<th height=\"60px\">").append("已还单量 Jumlah yang sudah Lunas")
                .append("<th height=\"60px\">").append("订单逾期率 Persentase Keterlambatan ")
                .append("<th height=\"60px\">").append("到期金额 Besaran Dana yang Harus Dilunasi (Tidak termasuk Biaya Keterlambatan/RMB)")
                .append("<th height=\"60px\">").append("还款金额(本金+服务费)Besaran Dana yang Sudah Dilunasi (Blm termausk Biaya Keterlambatan/RMB)")
                .append("<th height=\"60px\">").append("金额逾期率 Persentase Besaran Dana Keterlambatan (Tidak Termasuk Biaya Keterlambatan)")
        ;
        List<MoneyOverdueRate> moneyOverdueRateList = ordDao.getMoneyOverdueRate();
        for (MoneyOverdueRate info : moneyOverdueRateList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getDate())
                    .append("<td align=\"center\">").append(info.getOrders())
                    .append("<td align=\"center\">").append(info.getRepayOrder())
                    .append("<td align=\"center\">").append(info.getOrdOverDueRate())
                    .append("<td align=\"center\">").append(info.getDueAmount())
                    .append("<td align=\"center\">").append(info.getRepayAmount())
                    .append("<td align=\"center\">").append(info.getAmtOverDueRate())
            ;
        }
        sb.append("</table>");
//
        // 表3标题： 用户d1至d7逾期率（新户+老户）
        sb.append("<h3>3、用户d1至d7逾期率（新户+老户）Persentase Nasabah D1-D7 (Nasabah Baru dan Nasabah Lama)</h3>");
        // 表3表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th height=\"60px\">").append("到期日 Hari Jatuh Tempo")
                .append("<th height=\"60px\">").append("合计到期笔数 Total Nasabah yang Jatuh Tempo")
                .append("<th height=\"60px\">").append("D1逾期率 Persentase D1")
                .append("<th height=\"60px\">").append("D2逾期率 Persentase D2")
                .append("<th height=\"60px\">").append("D3逾期率 Persentase D3")
                .append("<th height=\"60px\">").append("D4逾期率 Persentase D4")
                .append("<th height=\"60px\">").append("D5逾期率 Persentase D5")
                .append("<th height=\"60px\">").append("D6逾期率 Persentase D6")
                .append("<th height=\"60px\">").append("D7逾期率 Persentase D7")
        ;
        List<NewAndOldD1D7OverdueRate> newAndOldD1D7OverdueRate = ordDao
                .getNewAndOldD1D7OverdueRate();
        for (NewAndOldD1D7OverdueRate info : newAndOldD1D7OverdueRate) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getDate())
                    .append("<td align=\"center\">").append(info.getExpirenum())
                    .append("<td align=\"center\">").append(info.getOverdue_d1())
                    .append("<td align=\"center\">").append(info.getOverdue_d2())
                    .append("<td align=\"center\">").append(info.getOverdue_d3())
                    .append("<td align=\"center\">").append(info.getOverdue_d4())
                    .append("<td align=\"center\">").append(info.getOverdue_d5())
                    .append("<td align=\"center\">").append(info.getOverdue_d6())
                    .append("<td align=\"center\">").append(info.getOverdue_d7())
            ;
        }
        sb.append("</table>");

        // 表4标题：新户d1至d7逾期率
        sb.append("<h3>4、新户d1至d7逾期率 Persentase D1-D7 Nasabah Baru</h3>");
        // 表4表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th height=\"60px\">").append("到期日 Hari Jatuh Tempo")
                .append("<th height=\"60px\">").append("新户到期笔数 Total Nasabah Baru yang Jatuh Tempo")
                .append("<th height=\"60px\">").append("新户D1逾期率 Persentase D1")
                .append("<th height=\"60px\">").append("新户D2逾期率 Persentase D2")
                .append("<th height=\"60px\">").append("新户D3逾期率 Persentase D3")
                .append("<th height=\"60px\">").append("新户D4逾期率 Persentase D4")
                .append("<th height=\"60px\">").append("新户D5逾期率 Persentase D5")
                .append("<th height=\"60px\">").append("新户D6逾期率 Persentase D6")
                .append("<th height=\"60px\">").append("新户D7逾期率 Persentase D7")
        ;
        List<NewD1D7OverdueRate> newD1D7OverdueRate = ordDao.getNewD1D7OverdueRate();
        for (NewD1D7OverdueRate info : newD1D7OverdueRate) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getArrivalDay())
                    .append("<td align=\"center\">").append(info.getNewArrivalNum())
                    .append("<td align=\"center\">").append(info.getNewD1OverdueRate())
                    .append("<td align=\"center\">").append(info.getNewD2OverdueRate())
                    .append("<td align=\"center\">").append(info.getNewD3OverdueRate())
                    .append("<td align=\"center\">").append(info.getNewD4OverdueRate())
                    .append("<td align=\"center\">").append(info.getNewD5OverdueRate())
                    .append("<td align=\"center\">").append(info.getNewD6OverdueRate())
                    .append("<td align=\"center\">").append(info.getNewD7OverdueRate())
            ;
        }
        sb.append("</table>");
//
        // 表5标题：老户d1至d7逾期率
        sb.append("<h3>5、老户d1至d7逾期率 Persentase D1-D7 Nasabah Lama</h3>");
        // 表5表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th height=\"60px\">").append("到期日 Hari Jatuh Tempo")
                .append("<th height=\"60px\">").append("老户到期笔数 Jumlah Nasabah Lama yang Jatuh Tempo ")
                .append("<th height=\"60px\">").append("老户D1逾期率 Persentase D1")
                .append("<th height=\"60px\">").append("老户D2逾期率 Persentase D2")
                .append("<th height=\"60px\">").append("老户D3逾期率 Persentase D3")
                .append("<th height=\"60px\">").append("老户D4逾期率 Persentase D4")
                .append("<th height=\"60px\">").append("老户D5逾期率 Persentase D5")
                .append("<th height=\"60px\">").append("老户D6逾期率 Persentase D6")
                .append("<th height=\"60px\">").append("老户D7逾期率 Persentase D7")
        ;
        List<OldD1D7OverdueRate> oldD1D7OverdueRate = ordDao.getOldD1D7OverdueRate();
        for (OldD1D7OverdueRate info : oldD1D7OverdueRate) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getArrivalDay())
                    .append("<td align=\"center\">").append(info.getOldArrivalNum())
                    .append("<td align=\"center\">").append(info.getOldD1OverdueRate())
                    .append("<td align=\"center\">").append(info.getOldD2OverdueRate())
                    .append("<td align=\"center\">").append(info.getOldD3OverdueRate())
                    .append("<td align=\"center\">").append(info.getOldD4OverdueRate())
                    .append("<td align=\"center\">").append(info.getOldD5OverdueRate())
                    .append("<td align=\"center\">").append(info.getOldD6OverdueRate())
                    .append("<td align=\"center\">").append(info.getOldD7OverdueRate())
            ;
        }
        sb.append("</table>");


        // 表6标题：-- 表6、新老用户d7逾期率
        sb.append("<h3>6、新老用户d7逾期率 Persentase D7 Nasabah Baru dan Lama </h3>");
        // 表6表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th height=\"60px\">").append("到期日 Hari Jatuh Tempo")
                .append("<th height=\"60px\">").append("新户到期笔数 Jumlah Nasabah Baru yang Diberi Pinjaman ")
                .append("<th height=\"60px\">").append("新户D1逾期率 Persentase D1 Nasabah Baru")
                .append("<th height=\"60px\">").append("新户D7逾期率 Persentase D7 Nasabah Baru")
                .append("<th height=\"60px\">").append("老户到期笔数 Jumlah Nasabah Lama yang Jatuh Tempo  ")
                .append("<th height=\"60px\">").append("老户D1逾期率 Persentase D1 Nasabah Lama ")
                .append("<th height=\"60px\">").append("老户D7逾期率 Persentase D7 Nasabah Lama ")
                .append("<th height=\"60px\">").append("合计到期笔数 Jumlah Nasabah yang Jatuh Tempo")
                .append("<th height=\"60px\">").append("合计D1逾期率 Total Persentase D1")
                .append("<th height=\"60px\">").append("合计D7逾期率 Total Persentase D7")
        ;
        List<NewAndOldD7OverdueRate> newAndOldD7OverdueRate = ordDao.getNewAndOldD7OverdueRate();
        for (NewAndOldD7OverdueRate info : newAndOldD7OverdueRate) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getP1())
                    .append("<td align=\"center\">").append(info.getP2())
                    .append("<td align=\"center\">").append(info.getP3())
                    .append("<td align=\"center\">").append(info.getP4())
                    .append("<td align=\"center\">").append(info.getP5())
                    .append("<td align=\"center\">").append(info.getP6())
                    .append("<td align=\"center\">").append(info.getP7())
                    .append("<td align=\"center\">").append(info.getP8())
                    .append("<td align=\"center\">").append(info.getP9())
                    .append("<td align=\"center\">").append(info.getP10())
            ;
        }
        sb.append("</table>");

        // 7、新老用户d15逾期率 --（新增）
        sb.append("<h3>7、新老用户D15逾期率 Persentase D15 Nasabah Baru dan Lama</h3>");
        // 表7表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th height=\"60px\">").append("到期日 Hari Jatuh Tempo")
                .append("<th height=\"60px\">").append("新户到期笔数 Jumlah Nasabah Baru yang Jatuh Tempo")
                .append("<th height=\"60px\">").append("新户D1逾期率 Persentase D1 Nasabah Baru")
                .append("<th height=\"60px\">").append("新户D7逾期率 Persentase D7 Nasabah Baru")
                .append("<th height=\"60px\">").append("新户D15逾期率 Persentase D15 Nasabah Baru")
                .append("<th height=\"60px\">").append("老户到期笔数 Persentase Nasabah Lama yang Jatuh Tempo")
                .append("<th height=\"60px\">").append("老户D1逾期率 Persentase D1 Nasabah Lama")
                .append("<th height=\"60px\">").append("老户D7逾期率 Persentase D7 Nasabah Baru")
                .append("<th height=\"60px\">").append("老户D15逾期率 Persentase D15 Nasabah Lama")
                .append("<th height=\"60px\">").append("合计到期笔数 Jumlah Nasabah yang Jatuh Tempo")
                .append("<th height=\"60px\">").append("合计D1逾期率 Total Persentase D1")
                .append("<th height=\"60px\">").append("合计D7逾期率 Total Persentase D7")
                .append("<th height=\"60px\">").append("合计D15逾期率 Total Persentase D15")
        ;
        List<NewDOverdueRate> rate1 = ordDao.getNewD15OverdueRate();
        for (NewDOverdueRate info : rate1) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getArrivalDay())
                    .append("<td align=\"center\">").append(info.getNewArrivalNum())
                    .append("<td align=\"center\">").append(info.getNewD1OverdueRate())
                    .append("<td align=\"center\">").append(info.getNewD7OverdueRate())
                    .append("<td align=\"center\">").append(info.getNewD15OverdueRate())
                    .append("<td align=\"center\">").append(info.getOldArrivalNum())
                    .append("<td align=\"center\">").append(info.getOldD1OverdueRate())
                    .append("<td align=\"center\">").append(info.getOldD7OverdueRate())
                    .append("<td align=\"center\">").append(info.getOldD15OverdueRate())
                    .append("<td align=\"center\">").append(info.getAllArrivalNum())
                    .append("<td align=\"center\">").append(info.getAllD1OverdueRate())
                    .append("<td align=\"center\">").append(info.getAllD7OverdueRate())
                    .append("<td align=\"center\">").append(info.getAllD15OverdueRate())
            ;
        }
        sb.append("</table>");

        //  8、新老用户d30逾期率 --（新增）
        sb.append("<h3>8、新老用户d30逾期率 Persentase D30 Nasabah Baru dan Lama</h3>");
        // 表8表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th height=\"60px\">").append("到期日 Hari Jatuh Tempo")
                .append("<th height=\"60px\">").append("新户到期笔数 Jumlah Nasabah Baru yang  Jatuh Tempo")
                .append("<th height=\"60px\">").append("新户D1逾期率 Persentase D1 Nasabah Baru")
                .append("<th height=\"60px\">").append("新户D7逾期率 Persentase D7 Nasabah Baru ")
                .append("<th height=\"60px\">").append("新户D15逾期率 Persetase d15 Nasabah Baru")
                .append("<th height=\"60px\">").append("新户D30逾期率 Persentase D30 Nasabah Baru ")
                .append("<th height=\"60px\">").append("老户到期笔数 Jumlah Nasabah Lama yang Jatuh Tempo")
                .append("<th height=\"60px\">").append("老户D1逾期率 Persentase D1 Nasabah Lama")
                .append("<th height=\"60px\">").append("老户D7逾期率 Persentase D7 Nasabah Lama")
                .append("<th height=\"60px\">").append("老户D15逾期率 Persentase D15 Nasabah Lama ")
                .append("<th height=\"60px\">").append("老户D30逾期率 Persentase D30 Nasabah Lama ")
                .append("<th height=\"60px\">").append("合计到期笔数 Total Persentase Nasabah yang Jatuh Tempo")
                .append("<th height=\"60px\">").append("合计D1逾期率 Total Persentase D1 ")
                .append("<th height=\"60px\">").append("合计D7逾期率 Total Persentase D7")
                .append("<th height=\"60px\">").append("合计D15逾期率 Total Persentase D15")
                .append("<th height=\"60px\">").append("合计D30逾期率 Total Persentase D30")
        ;
        List<NewDOverdueRate> rate2 = ordDao.getNewD30OverdueRate();
        for (NewDOverdueRate info : rate2) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getArrivalDay())
                    .append("<td align=\"center\">").append(info.getNewArrivalNum())
                    .append("<td align=\"center\">").append(info.getNewD1OverdueRate())
                    .append("<td align=\"center\">").append(info.getNewD7OverdueRate())
                    .append("<td align=\"center\">").append(info.getNewD15OverdueRate())
                    .append("<td align=\"center\">").append(info.getNewD30OverdueRate())
                    .append("<td align=\"center\">").append(info.getOldArrivalNum())
                    .append("<td align=\"center\">").append(info.getOldD1OverdueRate())
                    .append("<td align=\"center\">").append(info.getOldD7OverdueRate())
                    .append("<td align=\"center\">").append(info.getOldD15OverdueRate())
                    .append("<td align=\"center\">").append(info.getOldD30OverdueRate())
                    .append("<td align=\"center\">").append(info.getAllArrivalNum())
                    .append("<td align=\"center\">").append(info.getAllD1OverdueRate())
                    .append("<td align=\"center\">").append(info.getAllD7OverdueRate())
                    .append("<td align=\"center\">").append(info.getAllD15OverdueRate())
                    .append("<td align=\"center\">").append(info.getAllD30OverdueRate())
            ;
        }
        sb.append("</table>");

        // 9. 新老用户每日到期提前还款率
        sb.append("<h3>9、新老用户每日到期提前还款率  Persentase Pelunasan Naasabah Baru dan Lama yang Jatuh Tempo dan Melunasi Lebih Awal Per Hari</h3>");
        // 表9表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th height=\"60px\">").append("到期日 Hari Jatuh Tempo")
                .append("<th height=\"60px\">").append("新户到期笔数 Jumlah  Nasabah Baru yang Jatuh Tempo")
                .append("<th height=\"60px\">").append("新户d0之前还款 Nasabah Baru yang Melunasi Sebelum D0")
                .append("<th height=\"60px\">").append("新户提前还款率 Persentase Nasabah Baru yang Melunasi Lebih Awal")
                .append("<th height=\"60px\">").append("老户到期笔数 Jumlah Nasabah Lama yang Jatuh Tempo")
                .append("<th height=\"60px\">").append("老户d0之前还款 Nasabah Lama yang Melunasi Sebelum D0")
                .append("<th height=\"60px\">").append("老户提前还款率 Persentase Nasabah Lama yang Melunasi Lebih Awal ")
                .append("<th height=\"60px\">").append("合计到期笔数 Jumlah Nasabah yang Jatuh Tempo ")
                .append("<th height=\"60px\">").append("合计d0之前还款 Jumlah Nasabah yang Mengembalikan Sebelum D0 ")
                .append("<th height=\"60px\">").append("合计提前还款率 Persentase Nasabah yang Melunasi Lebih Awal ")
        ;
        List<DailybeforehandBackRate> dailybeforehandBackRate = ordDao.getDailybeforehandBackRate();
        for (DailybeforehandBackRate info : dailybeforehandBackRate) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getP1())
                    .append("<td align=\"center\">").append(info.getP2())
                    .append("<td align=\"center\">").append(info.getP3())
                    .append("<td align=\"center\">").append(info.getP4())
                    .append("<td align=\"center\">").append(info.getP5())
                    .append("<td align=\"center\">").append(info.getP6())
                    .append("<td align=\"center\">").append(info.getP7())
                    .append("<td align=\"center\">").append(info.getP8())
                    .append("<td align=\"center\">").append(info.getP9())
                    .append("<td align=\"center\">").append(info.getP10())
            ;
        }
        sb.append("</table>");

        // 10. 提额用户d1至d60逾期率
        sb.append("<h3>10、提额用户d1至d60逾期率</h3>");
        // 表10表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th height=\"60px\">").append("提额批次")
                .append("<th height=\"60px\">").append("D1到期笔数")
                .append("<th height=\"60px\">").append("D1逾期率")
                .append("<th height=\"60px\">").append("D7到期笔数")
                .append("<th height=\"60px\">").append("D7逾期率")
                .append("<th height=\"60px\">").append("D15到期笔数")
                .append("<th height=\"60px\">").append("D15逾期率")
                .append("<th height=\"60px\">").append("D30到期笔数")
                .append("<th height=\"60px\">").append("D30逾期率")
                .append("<th height=\"60px\">").append("D60到期笔数")
                .append("<th height=\"60px\">").append("D60逾期率")
        ;
        List<UpUserD1ToD7> upUserD1ToD7List = ordDao.getUpUserD1ToD7();
        for (UpUserD1ToD7 info : upUserD1ToD7List) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getP1())
                    .append("<td align=\"center\">").append(info.getD1ExpirationOrder())
                    .append("<td align=\"center\">").append(info.getD1OverdueRate())
                    .append("<td align=\"center\">").append(info.getD7ExpirationOrder())
                    .append("<td align=\"center\">").append(info.getD7OverdueRate())
                    .append("<td align=\"center\">").append(info.getD15ExpirationOrder())
                    .append("<td align=\"center\">").append(info.getD15OverdueRate())
                    .append("<td align=\"center\">").append(info.getD30ExpirationOrder())
                    .append("<td align=\"center\">").append(info.getD30OverdueRate())
                    .append("<td align=\"center\">").append(info.getD60ExpirationOrder())
                    .append("<td align=\"center\">").append(info.getD60OverdueRate())
            ;
        }
        sb.append("</table>");

        // 11. 提额用户的通过率
        sb.append("<h3>11、提额用户的通过率</h3>");
        // 表11表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th height=\"60px\">").append("提额批次")
                .append("<th height=\"60px\">").append("提额人数")
                .append("<th height=\"60px\">").append("申请人数")
                .append("<th height=\"60px\">").append("申请提交人数")
                .append("<th height=\"60px\">").append("通过人数")
                .append("<th height=\"60px\">").append("放款人数")
                .append("<th height=\"60px\">").append("提额申请率")
                .append("<th height=\"60px\">").append("申请提交率")
        ;
        List<UpUserOverRate> upUserOverRateList = ordDao.getUpUserOverRate();
        for (UpUserOverRate info : upUserOverRateList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getBatchId())
                    .append("<td align=\"center\">").append(info.getBatchNum())
                    .append("<td align=\"center\">").append(info.getApplyingNum())
                    .append("<td align=\"center\">").append(info.getCommitNum())
                    .append("<td align=\"center\">").append(info.getCheckPassNum())
                    .append("<td align=\"center\">").append(info.getLendSuccessNum())
                    .append("<td align=\"center\">").append(info.getBatchApplyingRate())
                    .append("<td align=\"center\">").append(info.getCommitRate())
            ;
        }
        sb.append("</table>");



        // 12. 提额至1000用户d1至d60逾期率
        sb.append("<h3>12、提额至1000用户d1至d60逾期率</h3>");
        // 表12表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th height=\"60px\">").append("提额批次")
                .append("<th height=\"60px\">").append("D1到期笔数")
                .append("<th height=\"60px\">").append("D1逾期率")
                .append("<th height=\"60px\">").append("D7到期笔数")
                .append("<th height=\"60px\">").append("D7逾期率")
                .append("<th height=\"60px\">").append("D15到期笔数")
                .append("<th height=\"60px\">").append("D15逾期率")
                .append("<th height=\"60px\">").append("D30到期笔数")
                .append("<th height=\"60px\">").append("D30逾期率")
                .append("<th height=\"60px\">").append("D60到期笔数")
                .append("<th height=\"60px\">").append("D60逾期率")
        ;
        List<UpUserD1ToD7> upUserD1ToD7ListWith1000Product = ordDao.getUpUserD1ToD7With1000Product();
        for (UpUserD1ToD7 info : upUserD1ToD7ListWith1000Product) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getP1())
                    .append("<td align=\"center\">").append(info.getD1ExpirationOrder())
                    .append("<td align=\"center\">").append(info.getD1OverdueRate())
                    .append("<td align=\"center\">").append(info.getD7ExpirationOrder())
                    .append("<td align=\"center\">").append(info.getD7OverdueRate())
                    .append("<td align=\"center\">").append(info.getD15ExpirationOrder())
                    .append("<td align=\"center\">").append(info.getD15OverdueRate())
                    .append("<td align=\"center\">").append(info.getD30ExpirationOrder())
                    .append("<td align=\"center\">").append(info.getD30OverdueRate())
                    .append("<td align=\"center\">").append(info.getD60ExpirationOrder())
                    .append("<td align=\"center\">").append(info.getD60OverdueRate())
            ;
        }
        sb.append("</table>");

        // 13. 提额至1000用户通过率
        sb.append("<h3>13、提额至1000用户通过率</h3>");
        // 表13表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th height=\"60px\">").append("提额批次")
                .append("<th height=\"60px\">").append("提额人数")
                .append("<th height=\"60px\">").append("申请人数")
                .append("<th height=\"60px\">").append("申请提交人数")
                .append("<th height=\"60px\">").append("通过人数")
                .append("<th height=\"60px\">").append("放款人数")
                .append("<th height=\"60px\">").append("提额申请率")
                .append("<th height=\"60px\">").append("申请提交率")
        ;
        List<UpUserOverRate> upUserOverRateListWith1000Product = ordDao.getUpUserOverRateWith1000Product();
        for (UpUserOverRate info : upUserOverRateListWith1000Product) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getBatchId())
                    .append("<td align=\"center\">").append(info.getBatchNum())
                    .append("<td align=\"center\">").append(info.getApplyingNum())
                    .append("<td align=\"center\">").append(info.getCommitNum())
                    .append("<td align=\"center\">").append(info.getCheckPassNum())
                    .append("<td align=\"center\">").append(info.getLendSuccessNum())
                    .append("<td align=\"center\">").append(info.getBatchApplyingRate())
                    .append("<td align=\"center\">").append(info.getCommitRate())
            ;
        }
        sb.append("</table>");

        return sb.toString();
    }

    /**
     * Do-It 每日审核时报
     */
    public String chackMailSender() {
        StringBuffer sb = new StringBuffer();

        // 表1.1标题：审核人员每日审核情况
        sb.append("<h3>1.1 审核人员每日任务与完成情况</h3>");
        // 表1.1表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("日期")
                .append("<th>").append("审核员")
                .append("<th>").append("初审分配数")
                .append("<th>").append("初审完成数")
                .append("<th>").append("初审通过数")
                .append("<th>").append("初审完成率")
                .append("<th>").append("初审通过率")
                .append("<th>").append("复审分配数")
                .append("<th>").append("复审完成数")
                .append("<th>").append("复审通过数")
                .append("<th>").append("复审完成率")
                .append("<th>").append("复审通过率")
        ;
        List<ReminderAndCheck> checkerDailyTaskAndCompletion = reminderAndCheckDao
                .getCheckerDailyTaskAndCompletion();
        for (ReminderAndCheck info : checkerDailyTaskAndCompletion) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getCreateDay())
                    .append("<td align=\"center\">").append(info.getRealName())
                    .append("<td align=\"center\">").append(info.getJuniorTask())
                    .append("<td align=\"center\">").append(info.getJuniorFinish())
                    .append("<td align=\"center\">").append(info.getJuniorPass())
                    .append("<td align=\"center\">").append(info.getJuniorFinishRate())
                    .append("<td align=\"center\">").append(info.getJuniorPassRate())
                    .append("<td align=\"center\">").append(info.getSeniorTask())
                    .append("<td align=\"center\">").append(info.getSeniorFinish())
                    .append("<td align=\"center\">").append(info.getSeniorPass())
                    .append("<td align=\"center\">").append(info.getSeniorFinishRate())
                    .append("<td align=\"center\">").append(info.getSeniorPassRate())
            ;
        }
        sb.append("</table>");

        // 表1.2标题：初审未审核与未分配单量
        sb.append("<h3>1.2 初审未审核与未分配单量</h3>");
        // 表1.2表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("进入审核日期")
                .append("<th>").append("审核类型")
                .append("<th>").append("未审核单量")
                .append("<th>").append("未分配单量")
        ;
        List<ReminderAndCheck> unfinished = reminderAndCheckDao.getUnfinished1();
        for (ReminderAndCheck info : unfinished) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getDate())
                    .append("<td align=\"center\">").append(info.getType())
                    .append("<td align=\"center\">").append(info.getNoFinishNum())
                    .append("<td align=\"center\">").append(info.getNoTaskNum())
            ;
        }
        sb.append("</table>");


        // 表1.3标题：复审未审核与未分配单量
        sb.append("<h3>1.3 复审未审核与未分配单量</h3>");
        // 表1.3表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("进入审核日期")
                .append("<th>").append("审核类型")
                .append("<th>").append("未审核单量")
                .append("<th>").append("未分配单量")
        ;
        List<ReminderAndCheck> unfinished2 = reminderAndCheckDao.getUnfinished2();
        for (ReminderAndCheck info : unfinished2) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getDate())
                    .append("<td align=\"center\">").append(info.getType())
                    .append("<td align=\"center\">").append(info.getNoFinishNum())
                    .append("<td align=\"center\">").append(info.getNoTaskNum())
            ;
        }
        sb.append("</table>");


        // 表2.1标题：初审命中各个规则的问题描述
        sb.append("<h3>2.1 初审命中各个规则的问题描述</h3>");
        // 表2.1表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("日期")
                .append("<th>").append("规则类型")
                .append("<th>").append("规则问题描述")
                .append("<th>").append("命中订单")
        ;
        List<ReminderAndCheck> problemDesc = reminderAndCheckDao.getProblemDesc();
        for (ReminderAndCheck info : problemDesc) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getDate())
                    .append("<td align=\"center\">").append(info.getRuleType())
                    .append("<td align=\"center\">").append(info.getDescription())
                    .append("<td align=\"center\">").append(info.getHitOrders())
            ;
        }
        sb.append("</table>");

        // 表2.2标题：电核接通率表
        sb.append("<h3>2.2 电核接通率表</h3>");
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("电核日期")
                .append("<th>").append("company电核数")
                .append("<th>").append("company接通率%")
                .append("<th>").append("FIRST电核数")
                .append("<th>").append("FIRST接通率%")
                .append("<th>").append("SECOND电核数")
                .append("<th>").append("SECOND接通率%")
                .append("<th>").append("累计电核数")
        ;
        List<ReminderAndCheck> problem14 = reminderAndCheckDao.getProblemDesc14();
        for (ReminderAndCheck info : problem14) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getPhoneCheckDate())
                    .append("<td align=\"center\">").append(info.getCompanyCheckNumber())
                    .append("<td align=\"center\">").append(info.getCompanyPhoneSuccessRate())
                    .append("<td align=\"center\">").append(info.getFIRSTPhoneCheckNumber())
                    .append("<td align=\"center\">").append(info.getFIRSTPhoneSuccessRate())
                    .append("<td align=\"center\">").append(info.getSECONDPhoneCheckNumber())
                    .append("<td align=\"center\">").append(info.getSECONDPhoneSuccessRate())
                    .append("<td align=\"center\">").append(info.getCumulativeNumberOfPhoneCheck())
            ;
        }
        sb.append("</table>");

        // 表2.3 标题：接通电核的通过率表
        sb.append("<h3>2.3 接通电核的通过率表</h3>");
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("电核日期")
                .append("<th>").append("电核结果")
                .append("<th>").append("company电核数")
                .append("<th>").append("company占比%")
                .append("<th>").append("first电核数")
                .append("<th>").append("first占比%")
                .append("<th>").append("second电核数")
                .append("<th>").append("second占比%")
        ;
        List<ReminderAndCheck> problem15 = reminderAndCheckDao.getProblemDesc15();
        for (ReminderAndCheck info : problem15) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getPhoneCheckDate())
                    .append("<td align=\"center\">").append(info.getPhoneCheckResult())
                    .append("<td align=\"center\">").append(info.getCompanyPhoneCheckNumber())
                    .append("<td align=\"center\">").append(info.getP1())
                    .append("<td align=\"center\">").append(info.getFIRSTPhoneCheckNumber())
                    .append("<td align=\"center\">").append(info.getF1())
                    .append("<td align=\"center\">").append(info.getSECONDPhoneCheckNumber())
                    .append("<td align=\"center\">").append(info.getS1())
            ;
        }
        sb.append("</table>");

        // 表2.4 标题：接通电核的拒绝原因表
        sb.append("<h3>2.4 接通电核的拒绝原因表</h3>");
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
            .append("<th>").append("拒绝日期")
            .append("<th>").append("拒绝原因")
            .append("<th>").append("拒绝订单")
            .append("<th>").append("拒绝订单占比")
        ;
        List<ReminderAndCheck> problem16 = reminderAndCheckDao.getProblemDesc16();
        for (ReminderAndCheck info : problem16) {
            sb.append("<tr>")
                .append("<td align=\"center\">").append(info.getRejectDate())
                .append("<td align=\"center\">").append(info.getRejectReason())
                .append("<td align=\"center\">").append(info.getRejectOrders())
                .append("<td align=\"center\">").append(info.getProportion())
            ;
        }
        sb.append("</table>");

        // 表2.5标题：审核员电核接通率表
        sb.append("<h3>2.5 审核员电核接通率表</h3>");
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
            .append("<th>").append("电核日期")
            .append("<th>").append("审核员")
            .append("<th>").append("company电核数")
            .append("<th>").append("company接通率")
            .append("<th>").append("FIRST接通数")
            .append("<th>").append("FIRST电核数")
            .append("<th>").append("FIRST接通率%")
            .append("<th>").append("SECOND接通数")
                .append("<th>").append("SECOND电核数")
                .append("<th>").append("SECOND接通率%")
                .append("<th>").append(" 累计电核数")
        ;
        List<ReminderAndCheck> problem17 = reminderAndCheckDao.getProblemDesc17();
        for (ReminderAndCheck info : problem17) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getPhoneCheckDate())
                    .append("<td align=\"center\">").append(info.getReviewers())
                    .append("<td align=\"center\">").append(info.getCompanyPhoneCheckNumber())
                    .append("<td align=\"center\">").append(info.getCompanyPhoneSuccessRate())
                    .append("<td align=\"center\">").append(info.getFIRSTPhoneSuccessNumber())
                    .append("<td align=\"center\">").append(info.getFIRSTPhoneCheckNumber())
                    .append("<td align=\"center\">").append(info.getFIRSTPhoneSuccessRate())
                    .append("<td align=\"center\">").append(info.getSECONDPhoneSuccessNumber())
                    .append("<td align=\"center\">").append(info.getSECONDPhoneCheckNumber())
                    .append("<td align=\"center\">").append(info.getSECONDPhoneSuccessRate())
                    .append("<td align=\"center\">").append(info.getCumulativeNumberOfPhoneCheck())
            ;
        }
        sb.append("</table>");

        // 表2.6标题：公司电话的平均拨打次数表
        sb.append("<h3>2.6 公司电话的平均拨打次数表</h3>");
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("拨打日期")
                .append("<th>").append("审核员")
                .append("<th>").append("公司订单数")
                .append("<th>").append("公司拨打次数")
                .append("<th>").append("first订单数")
                .append("<th>").append("first拨打次数")
                .append("<th>").append("second订单数")
                .append("<th>").append("second拨打次数")
        ;
        List<ReminderAndCheck> problem18 = reminderAndCheckDao.getProblemDesc18();
        for (ReminderAndCheck info : problem18) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getCallDate())
                    .append("<td align=\"center\">").append(info.getReviewers())
                    .append("<td align=\"center\">").append(info.getCompanyOrdersNumber())
                    .append("<td align=\"center\">").append(info.getNumberOfCallsMadeByCompanyPhone())
                    .append("<td align=\"center\">").append(info.getFirstOrdersNumber())
                    .append("<td align=\"center\">").append(info.getFirstCallNumber())
                    .append("<td align=\"center\">").append(info.getSecondOrdersNumber())
                    .append("<td align=\"center\">").append(info.getSecondCallNumber())
            ;
        }
        sb.append("</table>");

        return sb.toString();
    }

    /**
     * Do-It 风控内部邮件
     */
    public String riskInMailSender() {

        StringBuffer sb = new StringBuffer();
        Date today = new Date();
        Date chinaDate = DateUtils.addDateWithHour(today, 1);
        String todayStr = DateUtils.DateToString2(chinaDate);
        // 邮件前面的说明
        sb.append("<p style='color:#F00'>请注意保密</p>")
                .append("<p style='color:#F00'>统计时间截止到中国时间" + todayStr + "</p>")
                .append("<p style='text-align:right'>统计数据来自--张闪闪</p>");

        // 表1标题：新户_600产品机审拒绝原因
        sb.append("<h3>1、新户_600产品机审拒绝原因 </h3>");
        // 表1表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("拒绝日期")
                .append("<th>").append("拒绝原因")
                .append("<th>").append("拒绝订单数")
                .append("<th>").append("提交数")
                .append("<th>").append("在提交订单中的命中率")
        ;
        List<RegisterNum2> registerNumList = ordBlackDao.getRefusedDailyNumNew();
        for (RegisterNum2 info : registerNumList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getRefuseDate())
                    .append("<td align=\"center\">").append(info.getRefuseReason())
                    .append("<td align=\"center\">").append(info.getRefuseNum())
                    .append("<td align=\"center\">").append(info.getCommitNum())
                    .append("<td align=\"center\">").append(info.getCommitRefuseRate())
            ;
        }
        sb.append("</table>");

        // 表2标题：150产品机审拒绝原因
        sb.append("<h3>2、150产品机审拒绝原因 </h3>");
        // 表2表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("拒绝日期")
                .append("<th>").append("拒绝原因")
                .append("<th>").append("拒绝订单数")
                .append("<th>").append("进入150")
                .append("<th>").append("在进入150订单中的命中率")
        ;
        List<RefusedReasonWith150Product> refusedReasonWith150ProductList = ordBlackDao.getRefusedReasonWith150Product();
        for (RefusedReasonWith150Product info : refusedReasonWith150ProductList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getRefuseDate())
                    .append("<td align=\"center\">").append(info.getRefuseReason())
                    .append("<td align=\"center\">").append(info.getRefuseNum())
                    .append("<td align=\"center\">").append(info.getGetIntoProduct150())
                    .append("<td align=\"center\">").append(info.getRefuseRateProduct150())
            ;
        }
        sb.append("</table>");

        // 表3标题：80产品机审拒绝原因
        sb.append("<h3>3、80产品机审拒绝原因 </h3>");
        // 表3表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("拒绝日期")
                .append("<th>").append("拒绝原因")
                .append("<th>").append("拒绝订单数")
                .append("<th>").append("进入80")
                .append("<th>").append("在进入80订单中的命中率")
        ;
        List<RefusedReasonWith80Product> refusedReasonWith80ProductList = ordBlackDao.getRefusedReasonWith80Product();
        for (RefusedReasonWith80Product info : refusedReasonWith80ProductList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getRefuseDate())
                    .append("<td align=\"center\">").append(info.getRefuseReason())
                    .append("<td align=\"center\">").append(info.getRefuseNum())
                    .append("<td align=\"center\">").append(info.getGetIntoProduct80())
                    .append("<td align=\"center\">").append(info.getRefuseRateProduct80())
            ;
        }
        sb.append("</table>");

        // 表4标题：每日放款笔数_新户
        sb.append("<h3>4、每日放款笔数_新户 </h3>");
        // 表4表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("放款日期")
                .append("<th>").append("新户合计")
                .append("<th>").append("新户600")
                .append("<th>").append("新户150")
                .append("<th>").append("新户80")
                .append("<th>").append("600占比")
                .append("<th>").append("150占比")
                .append("<th>").append("80占比")
        ;
        List<LoanCountWithNewUser> loanCountWithNewUserList = ordBlackDao.getLoanCountWithNewUser();
        for (LoanCountWithNewUser info : loanCountWithNewUserList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getLendDate())
                    .append("<td align=\"center\">").append(info.getNewTotalLendNum())
                    .append("<td align=\"center\">").append(info.getNew600())
                    .append("<td align=\"center\">").append(info.getNew150())
                    .append("<td align=\"center\">").append(info.getNew80())
                    .append("<td align=\"center\">").append(info.getProportion1())
                    .append("<td align=\"center\">").append(info.getProportion2())
                    .append("<td align=\"center\">").append(info.getProportion3())
            ;
        }
        sb.append("</table>");

        // 表5标题：每日放款笔数_老户
        sb.append("<h3>5、每日放款笔数_老户 </h3>");
        // 表5表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("放款日期")
                .append("<th>").append("老户合计")
                .append("<th>").append("老户80")
                .append("<th>").append("老户200")
                .append("<th>").append("老户400")
                .append("<th>").append("老户600")
                .append("<th>").append("老户750")
                .append("<th>").append("老户1000")
                .append("<th>").append("老户其他金额")
                .append("<th>").append("80占比")
                .append("<th>").append("200占比")
                .append("<th>").append("400占比")
                .append("<th>").append("600占比")
                .append("<th>").append("750占比")
                .append("<th>").append("1000占比")
                .append("<th>").append("其他金额占比")
        ;
        List<LoanCountWithOldUser> loanCountWithOldUserList = ordBlackDao.getLoanCountWithOldUser();
        for (LoanCountWithOldUser info : loanCountWithOldUserList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getLendDate())
                    .append("<td align=\"center\">").append(info.getOldTotalLendNum())
                    .append("<td align=\"center\">").append(info.getOld80())
                    .append("<td align=\"center\">").append(info.getOld200())
                    .append("<td align=\"center\">").append(info.getOld400())
                    .append("<td align=\"center\">").append(info.getOld600())
                    .append("<td align=\"center\">").append(info.getOld750())
                    .append("<td align=\"center\">").append(info.getOld1000())
                    .append("<td align=\"center\">").append(info.getOtherAmout())
                    .append("<td align=\"center\">").append(info.getProportion80())
                    .append("<td align=\"center\">").append(info.getProportion200())
                    .append("<td align=\"center\">").append(info.getProportion400())
                    .append("<td align=\"center\">").append(info.getProportion600())
                    .append("<td align=\"center\">").append(info.getProportion750())
                    .append("<td align=\"center\">").append(info.getProportion1000())
                    .append("<td align=\"center\">").append(info.getOtherAmountproportion())
            ;
        }
        sb.append("</table>");

        // 表6标题：每日放款金额_新户 （单位：万/RMB）
        sb.append("<h3>6、每日放款金额_新户 （单位：万/RMB） </h3>");
        // 表6表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("放款日期")
                .append("<th>").append("新户合计金额")
                .append("<th>").append("新户600")
                .append("<th>").append("新户150")
                .append("<th>").append("新户80")
                .append("<th>").append("600占比")
                .append("<th>").append("150占比")
                .append("<th>").append("80占比")
        ;
        List<LoanAmoutWithNewUser> loanAmoutWithNewUserList = ordBlackDao.getLoanAmoutWithNewUser();
        for (LoanAmoutWithNewUser info : loanAmoutWithNewUserList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getLendDate())
                    .append("<td align=\"center\">").append(info.getNewTotalAmount())
                    .append("<td align=\"center\">").append(info.getNew600())
                    .append("<td align=\"center\">").append(info.getNew150())
                    .append("<td align=\"center\">").append(info.getNew80())
                    .append("<td align=\"center\">").append(info.getProportion600())
                    .append("<td align=\"center\">").append(info.getProportion150())
                    .append("<td align=\"center\">").append(info.getProportion80())
            ;
        }
        sb.append("</table>");

        // 表7标题：每日放款金额_老户 （单位：万/RMB）
        sb.append("<h3>7、每日放款金额_老户 （单位：万/RMB） </h3>");
        // 表7表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("放款日期")
                .append("<th>").append("老户合计金额")
                .append("<th>").append("老户80")
                .append("<th>").append("老户200")
                .append("<th>").append("老户400")
                .append("<th>").append("老户600")
                .append("<th>").append("老户750")
                .append("<th>").append("老户1000")
                .append("<th>").append("老户其他金额")
                .append("<th>").append("80占比")
                .append("<th>").append("200占比")
                .append("<th>").append("400占比")
                .append("<th>").append("600占比")
                .append("<th>").append("750占比")
                .append("<th>").append("1000占比")
                .append("<th>").append("其他金额占比")
        ;
        List<LoanAmoutWithOldUser> loanAmoutWithOldUserList = ordBlackDao.getLoanAmoutWithOldUser();
        for (LoanAmoutWithOldUser info : loanAmoutWithOldUserList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getLendDate())
                    .append("<td align=\"center\">").append(info.getOldTotalAmount())
                    .append("<td align=\"center\">").append(info.getOld80())
                    .append("<td align=\"center\">").append(info.getOld200())
                    .append("<td align=\"center\">").append(info.getOld400())
                    .append("<td align=\"center\">").append(info.getOld600())
                    .append("<td align=\"center\">").append(info.getOld750())
                    .append("<td align=\"center\">").append(info.getOld1000())
                    .append("<td align=\"center\">").append(info.getOtherAmount())
                    .append("<td align=\"center\">").append(info.getProportion80())
                    .append("<td align=\"center\">").append(info.getProportion200())
                    .append("<td align=\"center\">").append(info.getProportion400())
                    .append("<td align=\"center\">").append(info.getProportion600())
                    .append("<td align=\"center\">").append(info.getProportion750())
                    .append("<td align=\"center\">").append(info.getProportion1000())
                    .append("<td align=\"center\">").append(info.getOtherAmountProportion())
            ;
        }
        sb.append("</table>");

        // 表8标题：各环节当天申请订单的当前通过笔数（非cashcash_新户600产品）
        sb.append("<h3>8、各环节当天申请订单的当前通过笔数（非cashcash_新户600产品） </h3>");
        // 表8表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("申请日期")
                .append("<th>").append("申请订单")
                .append("<th>").append("提交订单")
                .append("<th>").append("机审通过")
                .append("<th>").append("进入初审")
                .append("<th>").append("初审完成")
                .append("<th>").append("初审完成本人外呼")
                .append("<th>").append("进入复审")
                .append("<th>").append("复审完成")
                .append("<th>").append("复审通过")
                .append("<th>").append("风控通过")
                .append("<th>").append("多头黑名单拒绝")
                .append("<th>").append("风控and黑名单通过")
                .append("<th>").append("待放款")
                .append("<th>").append("放款成功")
        ;
        List<OrdPassCountWithStage> ordPassCountWithStageList = ordBlackDao.getOrdPassCountWithStage();
        for (OrdPassCountWithStage info : ordPassCountWithStageList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getApplyDate())
                    .append("<td align=\"center\">").append(info.getApplyNum())
                    .append("<td align=\"center\">").append(info.getCommmitNum())
                    .append("<td align=\"center\">").append(info.getAutoCheckPass())
                    .append("<td align=\"center\">").append(info.getIntoFC())
                    .append("<td align=\"center\">").append(info.getFCfinish())
                    .append("<td align=\"center\">").append(info.getFCPassOutCall())
                    .append("<td align=\"center\">").append(info.getIntoSC())
                    .append("<td align=\"center\">").append(info.getSCfinish())
                    .append("<td align=\"center\">").append(info.getSCpass())
                    .append("<td align=\"center\">").append(info.getRiskPass())
                    .append("<td align=\"center\">").append(info.getBlackListMultiHeadNum())
                    .append("<td align=\"center\">").append(info.getRiskBlacklistPassNum())
                    .append("<td align=\"center\">").append(info.getLendNum())
                    .append("<td align=\"center\">").append(info.getLendSuccessNum())
            ;
        }
        sb.append("</table>");

        // 表9标题：各环节当天申请订单的当前通过率（非cashcash_新户600产品）
        sb.append("<h3>9、各环节当天申请订单的当前通过率（非cashcash_新户600产品） </h3>");
        // 表9表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("申请日期")
                .append("<th>").append("600申请提交率")
                .append("<th>").append("机审通过率")
                .append("<th>").append("人工初审通过率")
                .append("<th>").append("本人外呼通过率")
                .append("<th>").append("复审通过率")
                .append("<th>").append("风控通过率")
                .append("<th>").append("风控and黑名单通过率")
                .append("<th>").append("免审核占机审通过比例")
                .append("<th>").append("免审核占提交比例")
                .append("<th>").append("多头黑名单拒绝率")
                .append("<th>").append("放款成功率")
        ;
        List<OrdPassRateWithStage> ordPassRateWithStageList = ordBlackDao.getOrdPassRateWithStage();
        for (OrdPassRateWithStage info : ordPassRateWithStageList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getApplyDate())
                    .append("<td align=\"center\">").append(info.getCommitRate())
                    .append("<td align=\"center\">").append(info.getAutoCheckPassRate())
                    .append("<td align=\"center\">").append(info.getManFCPassRate())
                    .append("<td align=\"center\">").append(info.getSelfCallPassRate())
                    .append("<td align=\"center\">").append(info.getSCPassRate())
                    .append("<td align=\"center\">").append(info.getRiskPassRate())
                    .append("<td align=\"center\">").append(info.getRiskandBlackListPassRate())
                    .append("<td align=\"center\">").append(info.getNoMCRateInACpass())
                    .append("<td align=\"center\">").append(info.getNoMCrateInCommit())
                    .append("<td align=\"center\">").append(info.getBlackListMuliHeadRate())
                    .append("<td align=\"center\">").append(info.getLendSuccessRate())
            ;
        }
        sb.append("</table>");

        // 表10标题：各环节当天申请订单的当前通过笔数（cashcash_新户600产品）
        sb.append("<h3>10、各环节当天申请订单的当前通过笔数（cashcash_新户600产品） </h3>");
        // 表10表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("申请日期")
                .append("<th>").append("申请订单")
                .append("<th>").append("提交订单")
                .append("<th>").append("机审通过")
                .append("<th>").append("进入初审")
                .append("<th>").append("初审完成")
                .append("<th>").append("初审完成本人外呼")
                .append("<th>").append("进入复审")
                .append("<th>").append("复审完成")
                .append("<th>").append("复审通过")
                .append("<th>").append("风控通过")
                .append("<th>").append("多头黑名单拒绝")
                .append("<th>").append("风控and黑名单通过")
                .append("<th>").append("待放款")
                .append("<th>").append("放款成功")
        ;
        List<OrdPassCountWithStage> cash2OrdPassCountWithStageList = ordBlackDao.getCash2OrdPassCountWithStage();
        for (OrdPassCountWithStage info : cash2OrdPassCountWithStageList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getApplyDate())
                    .append("<td align=\"center\">").append(info.getApplyNum())
                    .append("<td align=\"center\">").append(info.getCommmitNum())
                    .append("<td align=\"center\">").append(info.getAutoCheckPass())
                    .append("<td align=\"center\">").append(info.getIntoFC())
                    .append("<td align=\"center\">").append(info.getFCfinish())
                    .append("<td align=\"center\">").append(info.getFCPassOutCall())
                    .append("<td align=\"center\">").append(info.getIntoSC())
                    .append("<td align=\"center\">").append(info.getSCfinish())
                    .append("<td align=\"center\">").append(info.getSCpass())
                    .append("<td align=\"center\">").append(info.getRiskPass())
                    .append("<td align=\"center\">").append(info.getBlackListMultiHeadNum())
                    .append("<td align=\"center\">").append(info.getRiskBlacklistPassNum())
                    .append("<td align=\"center\">").append(info.getLendNum())
                    .append("<td align=\"center\">").append(info.getLendSuccessNum())
            ;
        }
        sb.append("</table>");

        // 表11标题：各环节当天申请订单的当前通过率（cashcash_新户600产品）
        sb.append("<h3>11、各环节当天申请订单的当前通过率（cashcash_新户600产品） </h3>");
        // 表11表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("申请日期")
                .append("<th>").append("600申请提交率")
                .append("<th>").append("机审通过率")
                .append("<th>").append("人工初审通过率")
                .append("<th>").append("本人外呼通过率")
                .append("<th>").append("复审通过率")
                .append("<th>").append("风控通过率")
                .append("<th>").append("风控and黑名单通过率")
                .append("<th>").append("免审核占机审通过比例")
                .append("<th>").append("免审核占提交比例")
                .append("<th>").append("多头黑名单拒绝率")
                .append("<th>").append("放款成功率")
        ;
        List<OrdPassRateWithStage> cash2OrdPassRateWithStage = ordBlackDao.getCash2OrdPassRateWithStage();
        for (OrdPassRateWithStage info : cash2OrdPassRateWithStage) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getApplyDate())
                    .append("<td align=\"center\">").append(info.getCommitRate())
                    .append("<td align=\"center\">").append(info.getAutoCheckPassRate())
                    .append("<td align=\"center\">").append(info.getManFCPassRate())
                    .append("<td align=\"center\">").append(info.getSelfCallPassRate())
                    .append("<td align=\"center\">").append(info.getSCPassRate())
                    .append("<td align=\"center\">").append(info.getRiskPassRate())
                    .append("<td align=\"center\">").append(info.getRiskandBlackListPassRate())
                    .append("<td align=\"center\">").append(info.getNoMCRateInACpass())
                    .append("<td align=\"center\">").append(info.getNoMCrateInCommit())
                    .append("<td align=\"center\">").append(info.getBlackListMuliHeadRate())
                    .append("<td align=\"center\">").append(info.getLendSuccessRate())
            ;
        }
        sb.append("</table>");

        // 表12标题：每日规则拒绝人数统计（新户初审）
        sb.append("<h3>12、每日规则拒绝人数统计（新户初审）</h3>");
        // 表12表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("拒绝日期")
                .append("<th>").append("拒绝原因")
                .append("<th>").append("拒绝总人数")
                .append("<th>").append("拒绝人数")
                .append("<th>").append("拒绝占比 ")
        ;
        List<RegisterNum> registerNumList2 = ordBlackDao.getRefusedDailyNumNew2();
        for (RegisterNum info : registerNumList2) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getRefusedDate())
                    .append("<td align=\"center\">").append(info.getRefusedReason())
                    .append("<td align=\"center\">").append(info.getRefusedAllPeople())
                    .append("<td align=\"center\">").append(info.getRefusedPeople())
                    .append("<td align=\"center\">").append(info.getRejectrate())
            ;
        }
        sb.append("</table>");


        // 表13标题：每日规则拒绝人数统计（新户复审）
        sb.append("<h3>13、每日规则拒绝人数统计（新户复审）</h3>");
        // 表13表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("拒绝日期")
                .append("<th>").append("拒绝原因")
                .append("<th>").append("拒绝总人数")
                .append("<th>").append("拒绝人数")
                .append("<th>").append("拒绝占比 ")
        ;
        List<RegisterNum> registerNumList3= ordBlackDao.getRefusedDailyNumNew3();
        for (RegisterNum info : registerNumList3) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getRefusedDate())
                    .append("<td align=\"center\">").append(info.getRefusedReason())
                    .append("<td align=\"center\">").append(info.getRefusedAllPeople())
                    .append("<td align=\"center\">").append(info.getRefusedPeople())
                    .append("<td align=\"center\">").append(info.getRejectrate())
            ;
        }
        sb.append("</table>");

        // 表14标题：每日规则拒绝人数统计（老户）
        sb.append("<h3>14、每日规则拒绝人数统计（老户）</h3>");
        // 表14表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("拒绝日期")
                .append("<th>").append("拒绝原因")
                .append("<th>").append("拒绝总人数")
                .append("<th>").append("拒绝人数")
                .append("<th>").append("拒绝占比 ")
        ;
        List<RefusedDailyNum> refusedDailyNumList = ordBlackDao.getRefusedDailyNumOld();
        for (RefusedDailyNum info : refusedDailyNumList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getP1())
                    .append("<td align=\"center\">").append(info.getP2())
                    .append("<td align=\"center\">").append(info.getP3())
                    .append("<td align=\"center\">").append(info.getP4())
                    .append("<td align=\"center\">").append(info.getP5())
            ;
        }
        sb.append("</table>");


        // 表15标题： 新户免审核用户d1至d60逾期率
        sb.append("<h3>15、新户免审核用户d1至d60逾期率</h3>");
        // 表15表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("(免审核)到期星期")
                .append("<th>").append("D1到期笔数")
                .append("<th>").append("D1逾期率")
                .append("<th>").append("D7到期笔数")
                .append("<th>").append("D7逾期率")
                .append("<th>").append("D15到期笔数")
                .append("<th>").append("D15逾期率")
                .append("<th>").append("D30到期笔数")
                .append("<th>").append("D30逾期率")
                .append("<th>").append("D60到期笔数")
                .append("<th>").append("D60逾期率")
        ;
        List<NewUserWithNotCollectionD1ToD60> newUserWithNotCollectionD1ToD60List = ordDao
                .getNewUserWithNotCollectionD1ToD60();
        for (NewUserWithNotCollectionD1ToD60 info : newUserWithNotCollectionD1ToD60List) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getP1())
                    .append("<td align=\"center\">").append(info.getD1ExpirationOrder())
                    .append("<td align=\"center\">").append(info.getD1OverdueRate())
                    .append("<td align=\"center\">").append(info.getD7ExpirationOrder())
                    .append("<td align=\"center\">").append(info.getD7OverdueRate())
                    .append("<td align=\"center\">").append(info.getD15ExpirationOrder())
                    .append("<td align=\"center\">").append(info.getD15OverdueRate())
                    .append("<td align=\"center\">").append(info.getD30ExpirationOrder())
                    .append("<td align=\"center\">").append(info.getD30OverdueRate())
                    .append("<td align=\"center\">").append(info.getD60ExpirationOrder())
                    .append("<td align=\"center\">").append(info.getD60OverdueRate())
                    ;
        }
        sb.append("</table>");

        // 表16标题： 新户审核用户d1至d60逾期率
        sb.append("<h3>16、新户审核用户d1至d60逾期率</h3>");
        // 表16表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("(审核)到期星期")
                .append("<th>").append("D1到期笔数")
                .append("<th>").append("D1逾期率")
                .append("<th>").append("D7到期笔数")
                .append("<th>").append("D7逾期率")
                .append("<th>").append("D15到期笔数")
                .append("<th>").append("D15逾期率")
                .append("<th>").append("D30到期笔数")
                .append("<th>").append("D30逾期率")
                .append("<th>").append("D60到期笔数")
                .append("<th>").append("D60逾期率")
        ;
        List<NewUserWithNotCollectionD1ToD60> newUserWithCollectionD1ToD60List = ordDao
                .getNewUserWithCollectionD1ToD60();
        for (NewUserWithNotCollectionD1ToD60 info : newUserWithCollectionD1ToD60List) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getP1())
                    .append("<td align=\"center\">").append(info.getD1ExpirationOrder())
                    .append("<td align=\"center\">").append(info.getD1OverdueRate())
                    .append("<td align=\"center\">").append(info.getD7ExpirationOrder())
                    .append("<td align=\"center\">").append(info.getD7OverdueRate())
                    .append("<td align=\"center\">").append(info.getD15ExpirationOrder())
                    .append("<td align=\"center\">").append(info.getD15OverdueRate())
                    .append("<td align=\"center\">").append(info.getD30ExpirationOrder())
                    .append("<td align=\"center\">").append(info.getD30OverdueRate())
                    .append("<td align=\"center\">").append(info.getD60ExpirationOrder())
                    .append("<td align=\"center\">").append(info.getD60OverdueRate())
            ;
        }
        sb.append("</table>");

//        // 表7标题： 每周新户自动化比例，即免审核订单数/机审通过数(数据取2018年7月1日以后 对应2018年第26周起)：
//        sb.append("<h3>7、每周新户自动化比例</h3>");
//        // 表7表格体
//        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
//        sb.append("<tr>")
//                .append("<th>").append("日期")
//                .append("<th>").append("每周机审通过数")
//                .append("<th>").append("每周免审核订单数")
//                .append("<th>").append("自动化比例")
//        ;
//        List<WeekAutoReviewRate> weekAutoReviewRateList = ordDao
//                .getWeekAutoReviewRate();
//        for (WeekAutoReviewRate info : weekAutoReviewRateList) {
//            sb.append("<tr>")
//                    .append("<td align=\"center\">").append(info.getWeek())
//                    .append("<td align=\"center\">").append(info.getAutoCheckPassNum())
//                    .append("<td align=\"center\">").append(info.getNoManCheckPassNum())
//                    .append("<td align=\"center\">").append(info.getAutoRate())
//            ;
//        }
//        sb.append("</table>");

        // 表17标题： 600产品每周新户免审核比例
        sb.append("<h3>17、600产品每周新户免审核比例 </h3>");
        // 表17表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("日期")
                .append("<th>").append("600产品机审通过数")
                .append("<th>").append("600产品免审核订单数")
                .append("<th>").append("600产品免审核比例")
        ;
        List<WeekSixHundredProductNoReviewRate> weekSixHundredProductNoReviewRateList = ordDao
                .getWeekSixHundredProductNoReviewRate();
        for (WeekSixHundredProductNoReviewRate info : weekSixHundredProductNoReviewRateList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getWeek())
                    .append("<td align=\"center\">").append(info.getProAutoCheckPassNumber())
                    .append("<td align=\"center\">").append(info.getProNoManCheckNumber())
                    .append("<td align=\"center\">").append(info.getProAutoRate())
            ;
        }
        sb.append("</table>");

        // 表18标题： 每天因非本人规则被拒绝的订单数，进入初审的订单数，占比
        sb.append("<h3>18、每天因非本人规则被拒绝的订单数，进入初审的订单数，占比</h3>");
        // 表18表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("日期")
                .append("<th>").append("非本人订单数")
                .append("<th>").append("待初审订单数")
                .append("<th>").append("占比")
        ;
        List<RefuseRateWithNotSelfRule> refuseRateWithNotSelfRuleList = ordDao
                .getRefuseRateWithNotSelfRule();
        for (RefuseRateWithNotSelfRule info : refuseRateWithNotSelfRuleList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getDate())
                    .append("<td align=\"center\">").append(info.getRejectNum())
                    .append("<td align=\"center\">").append(info.getFirstCheckNum())
                    .append("<td align=\"center\">").append(info.getRate())
            ;
        }
        sb.append("</table>");

        // 表19标题： 各渠道每周机审通过率（取最近3周）
        sb.append("<h3>19、各渠道每周机审通过率（取最近3周）</h3>");
        // 表19表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("日期")
                .append("<th>").append("渠道")
                .append("<th>").append("提交订单数")
                .append("<th>").append("机审通过率")
        ;
        List<SourceDayPassRate> sourceDayPassRateList = ordDao
                .getSourceDayPassRate();
        for (SourceDayPassRate info : sourceDayPassRateList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getWeek())
                    .append("<td align=\"center\">").append(info.getSource())
                    .append("<td align=\"center\">").append(info.getCommitNum())
                    .append("<td align=\"center\">").append(info.getAutoCheckPassRate())
            ;
        }
        sb.append("</table>");

        // 表20标题： 每周各渠道到期笔数，D1，D7，D15逾期率 （取最近5周）
        sb.append("<h3>20、每周各渠道到期笔数，D1，D7，D15逾期率 （取最近5周）</h3>");
        // 表20表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("到期时间")
                .append("<th>").append("渠道")
                .append("<th>").append("到期笔数")
                .append("<th>").append("D1逾期率")
                .append("<th>").append("D7逾期率")
                .append("<th>").append("D15逾期率")
        ;
        List<SouceInDateNum> souceInDateNumList = ordDao
                .getSouceInDateNum();
        for (SouceInDateNum info : souceInDateNumList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getExpireWeek())
                    .append("<td align=\"center\">").append(info.getSource())
                    .append("<td align=\"center\">").append(info.getExpireNum())
                    .append("<td align=\"center\">").append(info.getD1overDueRate())
                    .append("<td align=\"center\">").append(info.getD7overDueRate())
                    .append("<td align=\"center\">").append(info.getD15overDueRate())
            ;
        }
        sb.append("</table>");

        // 表21标题： 聚信立税卡接口每天返回情况统计
        sb.append("<h3>21、聚信立税卡接口每天返回情况统计 </h3>");
        // 表21表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("日期")
                .append("<th>").append("返回结果")
                .append("<th>").append("数量")
                .append("<th>").append("总数")
                .append("<th>").append("比例")
        ;
        List<JXLDayResponse> JXLDayResponseList = ordDao
                .getJXLDayResponse();
        for (JXLDayResponse info : JXLDayResponseList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getDate())
                    .append("<td align=\"center\">").append(info.getResponse())
                    .append("<td align=\"center\">").append(info.getAmount())
                    .append("<td align=\"center\">").append(info.getTotal())
                    .append("<td align=\"center\">").append(info.getProportion())
            ;
        }
        sb.append("</table>");

        // 表22标题： 每日advance验证返回具体结果
        sb.append("<h3>22、每日advance验证返回具体结果 </h3>");
        // 表22表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("日期")
                .append("<th>").append("返回结果")
                .append("<th>").append("数量")
                .append("<th>").append("总数")
                .append("<th>").append("比例")
        ;
        List<JXLDayResponse> JXLDayResponseList2 = ordDao
                .getAdvanceResponse();
        for (JXLDayResponse info : JXLDayResponseList2) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getDate())
                    .append("<td align=\"center\">").append(info.getResponse())
                    .append("<td align=\"center\">").append(info.getAmount())
                    .append("<td align=\"center\">").append(info.getTotal())
                    .append("<td align=\"center\">").append(info.getProportion())
            ;
        }
        sb.append("</table>");

//        // 表23标题： 欺诈规则命中率
//        sb.append("<h3>23、欺诈规则命中率 </h3>");
//        // 表23表格体
//        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
//        sb.append("<tr>")
//                .append("<th>").append("日期")
//                .append("<th>").append("欺诈规则")
//                .append("<th>").append("命中人数")
//                .append("<th>").append("被拒笔数")
//                .append("<th>").append("欺诈规则机审拒绝率")
//        ;
//        List<FraudRule> fraudRuleList = riskDataSynService
//                .getFraudRuleData();
//        for (FraudRule rule : fraudRuleList) {
//            sb.append("<tr>")
//                    .append("<td align=\"center\">").append(rule.getDate())
//                    .append("<td align=\"center\">").append(rule.getFraudRule())
//                    .append("<td align=\"center\">").append(rule.getHitOrd())
//                    .append("<td align=\"center\">").append(rule.getRejectedOrd())
//                    .append("<td align=\"center\">").append(rule.getFraudRuleRejectRate())
//            ;
//        }
//        sb.append("</table>");

        return sb.toString();
    }


    /**
     * Do-It 风控外呼邮件
     */
    public String riskInMailSenderWithCall() {

        StringBuffer sb = new StringBuffer();

        // 表1标题：当前累计外呼通过率
        sb.append("<h3>1、当前累计外呼通过率 </h3>");
        // 表1表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("本人外呼日期")
                .append("<th>").append("外呼已发送未拿到报告订单数")
                .append("<th>").append("发送外呼请求失败订单数")
                .append("<th>").append("有效外呼订单数")
                .append("<th>").append("外呼接通订单数")
                .append("<th>").append("外呼继续拨打订单数")
                .append("<th>").append("外呼拒绝订单数")
                .append("<th>").append("外呼完成订单的接通率")
        ;
        List<RiskCall1> riskCall1List = ordBlackDao.getRiskCall1();
        for (RiskCall1 info : riskCall1List) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getCusNewCallDate())
                    .append("<td align=\"center\">").append(info.getCalledNoReportOrd())
                    .append("<td align=\"center\">").append(info.getCalledFailedOrd())
                    .append("<td align=\"center\">").append(info.getValidCallOrd())
                    .append("<td align=\"center\">").append(info.getConnectOrd())
                    .append("<td align=\"center\">").append(info.getKeepCallingOrd())
                    .append("<td align=\"center\">").append(info.getCallRejectOrd())
                    .append("<td align=\"center\">").append(info.getConnectRate())
            ;
        }
        sb.append("</table>");

        // 表2标题：当前累计外呼拒绝率详情
        sb.append("<h3>2、当前累计外呼拒绝率详情 </h3>");
        // 表2表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("本人外呼日期")
                .append("<th>").append("有效外呼订单数")
                .append("<th>").append("号码无效订单数")
                .append("<th>").append("运营商拒绝订单数")
                .append("<th>").append("号码不存在订单数")
                .append("<th>").append("本人号码外呼超过次数的订单")
                .append("<th>").append("外呼拒绝订单数")
                .append("<th>").append("号码不存在的拒绝率")
                .append("<th>").append("号码无效的拒绝率")
                .append("<th>").append("运营商拒绝的拒绝率")
                .append("<th>").append("本人号码外呼超过次数的订单拒绝率")
                .append("<th>").append("外呼拒绝率")
        ;
        List<RiskCall2> riskCall2List = ordBlackDao.getRiskCall2();
        for (RiskCall2 info : riskCall2List) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getCusNewCallDate())
                    .append("<td align=\"center\">").append(info.getValidCallOrd())
                    .append("<td align=\"center\">").append(info.getInvalidNumOrd())
                    .append("<td align=\"center\">").append(info.getOpeRejectOrd())
                    .append("<td align=\"center\">").append(info.getNotExistNumOrd())
                    .append("<td align=\"center\">").append(info.getOverTimesOrd())
                    .append("<td align=\"center\">").append(info.getCallRejectOrd())
                    .append("<td align=\"center\">").append(info.getNotExistNumRate())
                    .append("<td align=\"center\">").append(info.getInvalidNumRejectRate())
                    .append("<td align=\"center\">").append(info.getOpeRejectRate())
                    .append("<td align=\"center\">").append(info.getOverTimesRate())
                    .append("<td align=\"center\">").append(info.getCallRejectRate())
            ;
        }
        sb.append("</table>");

        // 表3标题：当天创建订单的外呼通过率--漏斗
        sb.append("<h3>3、当天创建订单的外呼通过率--漏斗 </h3>");
        // 表3表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("本人开始外呼日期")
                .append("<th>").append("外呼已发送未拿到报告订单数")
                .append("<th>").append("发送外呼请求失败订单数")
                .append("<th>").append("有效外呼订单数")
                .append("<th>").append("外呼接通订单数")
                .append("<th>").append("外呼继续拨打订单数")
                .append("<th>").append("外呼拒绝订单数")
                .append("<th>").append("外呼完成订单的接通率")
        ;
        List<RiskCall3> riskCall3List = ordBlackDao.getRiskCall3();
        for (RiskCall3 info : riskCall3List) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getP1())
                    .append("<td align=\"center\">").append(info.getP2())
                    .append("<td align=\"center\">").append(info.getP3())
                    .append("<td align=\"center\">").append(info.getP4())
                    .append("<td align=\"center\">").append(info.getP5())
                    .append("<td align=\"center\">").append(info.getP6())
                    .append("<td align=\"center\">").append(info.getP7())
                    .append("<td align=\"center\">").append(info.getP8())
            ;
        }
        sb.append("</table>");

        // 表4标题：当天创建订单的外呼拒绝率详情--漏斗
        sb.append("<h3>4、当天创建订单的外呼拒绝率详情--漏斗 </h3>");
        // 表4表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("本人开始外呼日期")
                .append("<th>").append("有效外呼订单数")
                .append("<th>").append("号码无效订单数")
                .append("<th>").append("运营商拒绝订单数")
                .append("<th>").append("号码不存在订单数")
                .append("<th>").append("本人号码外呼超过次数的订单")
                .append("<th>").append("外呼拒绝订单数")
                .append("<th>").append("号码不存在的拒绝率")
                .append("<th>").append("号码无效的拒绝率")
                .append("<th>").append("运营商拒绝的拒绝率")
                .append("<th>").append("本人号码外呼超过次数的订单拒绝率")
                .append("<th>").append("外呼拒绝率")
        ;
        List<RiskCall4> riskCall4List = ordBlackDao.getRiskCall4();
        for (RiskCall4 info : riskCall4List) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getP1())
                    .append("<td align=\"center\">").append(info.getP2())
                    .append("<td align=\"center\">").append(info.getP3())
                    .append("<td align=\"center\">").append(info.getP4())
                    .append("<td align=\"center\">").append(info.getP5())
                    .append("<td align=\"center\">").append(info.getP6())
                    .append("<td align=\"center\">").append(info.getP7())
                    .append("<td align=\"center\">").append(info.getP8())
                    .append("<td align=\"center\">").append(info.getP9())
                    .append("<td align=\"center\">").append(info.getP10())
                    .append("<td align=\"center\">").append(info.getP11())
                    .append("<td align=\"center\">").append(info.getP12())
            ;
        }
        sb.append("</table>");
//
//        // 表5标题：老户备选联系人外呼
//        sb.append("<h3>5、老户备选联系人外呼 </h3>");
//        // 表5表格体
//        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
//        sb.append("<tr>")
//                .append("<th>").append("外呼日期")
//                .append("<th>").append("老户备选联系人外呼订单数")
//                .append("<th>").append("老户外呼备选联系人至少3个号码完全有效")
//                .append("<th>").append("备选联系人号码完全有效>= 6")
//                .append("<th>").append("备选联系人号码完全有效>= 5")
//                .append("<th>").append("备选联系人号码完全有效>= 4")
//                .append("<th>").append("备选联系人号码完全有效>= 3")
//                .append("<th>").append("备选联系人号码完全有效>= 2")
//                .append("<th>").append("备选联系人号码完全有效>= 1")
//                .append("<th>").append("备选联系人号码完全有效>= 0")
//        ;
//        List<RiskCall5> riskCall5List = ordBlackDao.getRiskCall5();
//        for (RiskCall5 info : riskCall5List) {
//            sb.append("<tr>")
//                    .append("<td align=\"center\">").append(info.getCallDate())
//                    .append("<td align=\"center\">").append(info.getOldAlterCallTotal())
//                    .append("<td align=\"center\">").append(info.getOldAlterAtLeast3Rate())
//                    .append("<td align=\"center\">").append(info.getAlterEffectAtLeast6())
//                    .append("<td align=\"center\">").append(info.getAlterEffect5())
//                    .append("<td align=\"center\">").append(info.getAlterEffect4())
//                    .append("<td align=\"center\">").append(info.getAlterEffect3())
//                    .append("<td align=\"center\">").append(info.getAlterEffect2())
//                    .append("<td align=\"center\">").append(info.getAlterEffect1())
//                    .append("<td align=\"center\">").append(info.getAlterEffect0())
//            ;
//        }
//        sb.append("</table>");
//
//        // 表6标题：新户备选联系人外呼
//        sb.append("<h3>6、新户备选联系人外呼</h3>");
//        // 表6表格体
//        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
//        sb.append("<tr>")
//                .append("<th>").append("外呼日期")
//                .append("<th>").append("新户备选联系人外呼订单数")
//                .append("<th>").append("新户外呼备选联系人至少5个号码完全有效")
//                .append("<th>").append("备选联系人号码完全有效>= 6")
//                .append("<th>").append("备选联系人号码完全有效 = 5")
//                .append("<th>").append("备选联系人号码完全有效 = 4")
//                .append("<th>").append("备选联系人号码完全有效 = 3")
//                .append("<th>").append("备选联系人号码完全有效 = 2")
//                .append("<th>").append("备选联系人号码完全有效 = 1")
//                .append("<th>").append("备选联系人号码完全有效 = 0")
//        ;
//        List<RiskCall6> riskCall6List = ordBlackDao.getRiskCall6();
//        for (RiskCall6 info : riskCall6List) {
//            sb.append("<tr>")
//                    .append("<td align=\"center\">").append(info.getCallDate())
//                    .append("<td align=\"center\">").append(info.getNewAlterCallTotal())
//                    .append("<td align=\"center\">").append(info.getNewAlterAtLeast5Rate())
//                    .append("<td align=\"center\">").append(info.getAlterEffectAtLeast6())
//                    .append("<td align=\"center\">").append(info.getAlterEffect5())
//                    .append("<td align=\"center\">").append(info.getAlterEffect4())
//                    .append("<td align=\"center\">").append(info.getAlterEffect3())
//                    .append("<td align=\"center\">").append(info.getAlterEffect2())
//                    .append("<td align=\"center\">").append(info.getAlterEffect1())
//                    .append("<td align=\"center\">").append(info.getAlterEffect0())
//            ;
//        }
//        sb.append("</table>");

        // 表5标题：老户紧急联系人外呼
        sb.append("<h3>5、老户紧急联系人外呼</h3>");
        // 表5表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("外呼日期")
                .append("<th>").append("老户联系人外呼订单数")
                .append("<th>").append("有效4订单数")
                .append("<th>").append("有效3订单数")
                .append("<th>").append("有效2订单数")
                .append("<th>").append("有效1订单数")
                .append("<th>").append("有效0订单数")
                .append("<th>").append("有效号码_4的占比")
                .append("<th>").append("有效号码_3的占比")
                .append("<th>").append("有效号码_2的占比")
                .append("<th>").append("有效号码_1的占比")
                .append("<th>").append("有效号码_0的占比")
                .append("<th>").append("老户联系人外呼通过率")
        ;
        List<RiskCall7> riskCall7List = ordBlackDao.getRiskCall7();
        for (RiskCall7 info : riskCall7List) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getCallDate())
                    .append("<td align=\"center\">").append(info.getOldEmerCallNum())
                    .append("<td align=\"center\">").append(info.getEffect_4Num())
                    .append("<td align=\"center\">").append(info.getEffect_3Num())
                    .append("<td align=\"center\">").append(info.getEffect_2Num())
                    .append("<td align=\"center\">").append(info.getEffect_1Num())
                    .append("<td align=\"center\">").append(info.getEffect_0Num())
                    .append("<td align=\"center\">").append(info.getEffect_4rate())
                    .append("<td align=\"center\">").append(info.getEffect_3rate())
                    .append("<td align=\"center\">").append(info.getEffect_2rate())
                    .append("<td align=\"center\">").append(info.getEffect_1rate())
                    .append("<td align=\"center\">").append(info.getEffect_0rate())
                    .append("<td align=\"center\">").append(info.getOldEmerCallPassRate())
            ;
        }
        sb.append("</table>");

        // 表6标题：新户紧急联系人外呼
        sb.append("<h3>6、新户紧急联系人外呼</h3>");
        // 表6表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("外呼日期")
                .append("<th>").append("新户紧急联系人外呼订单数")
                .append("<th>").append("老户联系人外呼订单数")
                .append("<th>").append("有效4订单数")
                .append("<th>").append("有效3订单数")
                .append("<th>").append("有效2订单数")
                .append("<th>").append("有效1订单数")
                .append("<th>").append("有效0订单数")
                .append("<th>").append("有效号码_4的占比")
                .append("<th>").append("有效号码_3的占比")
                .append("<th>").append("有效号码_2的占比")
                .append("<th>").append("有效号码_1的占比")
                .append("<th>").append("有效号码_0的占比")
                .append("<th>").append("新户联系人外呼通过率")
        ;
        List<RiskCall8> riskCall8List = ordBlackDao.getRiskCall8();
        for (RiskCall8 info : riskCall8List) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getCallDate())
                    .append("<td align=\"center\">").append(info.getNewEmerCallNum())
                    .append("<td align=\"center\">").append(info.getOldEmerCallNum())
                    .append("<td align=\"center\">").append(info.getEffect_4Num())
                    .append("<td align=\"center\">").append(info.getEffect_3Num())
                    .append("<td align=\"center\">").append(info.getEffect_2Num())
                    .append("<td align=\"center\">").append(info.getEffect_1Num())
                    .append("<td align=\"center\">").append(info.getEffect_0Num())
                    .append("<td align=\"center\">").append(info.getEffect_4rate())
                    .append("<td align=\"center\">").append(info.getEffect_3rate())
                    .append("<td align=\"center\">").append(info.getEffect_2rate())
                    .append("<td align=\"center\">").append(info.getEffect_1rate())
                    .append("<td align=\"center\">").append(info.getEffect_0rate())
                    .append("<td align=\"center\">").append(info.getOldEmerCallPassRate())
            ;
        }
        sb.append("</table>");

        // 表7标题：inforbip 每日紧急联系人外呼结果（按号码）
        sb.append("<h3>7、inforbip 每日紧急联系人外呼结果（按号码）</h3>");
        // 表7表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("外呼日期")
                .append("<th>").append("inforbip紧急联系人外呼号码个数")
                .append("<th>").append("完全有效占比")
                .append("<th>").append("可能有效占比")
                .append("<th>").append("无效占比")
        ;
        List<RiskCall9> riskCall9List = ordBlackDao.getRiskCall9();
        for (RiskCall9 info : riskCall9List) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getCalldate())
                    .append("<td align=\"center\">").append(info.getInforbipEmerCallNum())
                    .append("<td align=\"center\">").append(info.getEffectRate())
                    .append("<td align=\"center\">").append(info.getMaybeEffectRate())
                    .append("<td align=\"center\">").append(info.getInvalidRate())
            ;
        }
        sb.append("</table>");

        // 表8标题：twilio 每日紧急联系人外呼结果（按号码）
        sb.append("<h3>8、twilio 每日紧急联系人外呼结果（按号码）</h3>");
        // 表8表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("外呼日期")
                .append("<th>").append("twilio紧急联系人外呼号码个数")
                .append("<th>").append("完全有效占比")
                .append("<th>").append("可能有效_busy占比")
                .append("<th>").append("可能有效_NoAnswer占比")
                .append("<th>").append("判定为有效号码的比例")
                .append("<th>").append("无效占比")
        ;
        List<RiskCall10> riskCall10List = ordBlackDao.getRiskCall10();
        for (RiskCall10 info : riskCall10List) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getCalldate())
                    .append("<td align=\"center\">").append(info.getTwilioEmerCallNum())
                    .append("<td align=\"center\">").append(info.getTotalEffectRate())
                    .append("<td align=\"center\">").append(info.getMaybeEffect_busyRate())
                    .append("<td align=\"center\">").append(info.getMaybeEffect_NoAnswerRate())
                    .append("<td align=\"center\">").append(info.getEffectRate())
                    .append("<td align=\"center\">").append(info.getInvalidRate())
            ;
        }
        sb.append("</table>");

        // 表9标题：公司外呼返回结果
        sb.append("<h3>8、公司外呼返回结果 </h3>");
        // 表9表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("外呼日期")
                .append("<th>").append("公司外呼总数")
                .append("<th>").append("完全有效")
                .append("<th>").append("可能有效")
                .append("<th>").append("无效")
                .append("<th>").append("完全有效占比")
                .append("<th>").append("可能有效占比")
                .append("<th>").append("无效占比")
        ;
        List<RiskCall11> riskCall11List = ordBlackDao.getRiskCall11();
        for (RiskCall11 info : riskCall11List) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getCallDate())
                    .append("<td align=\"center\">").append(info.getCompanyCallNum())
                    .append("<td align=\"center\">").append(info.getTotalEffect())
                    .append("<td align=\"center\">").append(info.getMaybeEffect())
                    .append("<td align=\"center\">").append(info.getInvalid())
                    .append("<td align=\"center\">").append(info.getTotalEffectRate())
                    .append("<td align=\"center\">").append(info.getMaybeEffectRate())
                    .append("<td align=\"center\">").append(info.getInvalidRate())
            ;
        }
        sb.append("</table>");

        return sb.toString();
    }

    /**
     * Doit 用户每日逾期率
     */
    public String doitDayOverdueRate() {
        List<OverDueRateForMail>  rateList = this.ordDao.getDayOverdueRate();
        List<ExcelData> excelDataList = getExcelModel(rateList);
        return excelUtil.createExcelFile(ExecelFileType.dayOverdue_template,excelDataList,new Date());
    }

    /**
     * Doit 用户每日逾期率(新户)
     */
    public String doitDayOverdueRateWithNewUser() {
        List<OverDueRateForMail>  rateList1 = this.ordDao.getDayOverdueRateWithNewUser();
        List<ExcelData> excelDataList = getExcelModel(rateList1);
        return excelUtil.createExcelFile(ExecelFileType.dayOverdueNew_template,excelDataList,new Date());
    }

    /**
     * Doit 用户每日逾期率（老户）
     */
    public String doitDayOverdueRateWithOldUser() {
        List<OverDueRateForMail>  rateList2 = this.ordDao.getDayOverdueRateWithOldUser();
        List<ExcelData> excelDataList = getExcelModel(rateList2);
        return excelUtil.createExcelFile(ExecelFileType.dayOverdueOld_template,excelDataList,new Date());
    }

    /**
     * Doit 运营每日外呼号码
     */
    public String doitCallNumber() {
        List<DayCallNumber>  callNumberList = this.ordDao.getDayCallNumber();
        log.info("callNumberList的号码数量为"+callNumberList.size());
        for (DayCallNumber number:callNumberList){
            number.setPhone(DESUtils.decrypt(number.getMobileDes()));
        }
        List<ExcelData> excelDataList = getCallNumberModel(callNumberList);
        log.info("excelDataList的号码数量为"+callNumberList.size());
        return excelUtil.createExcelFile(ExecelFileType.dayCallNumber_template,excelDataList,new Date());
    }

    //用户逾期率Excel模板（新sql）
    private List<ExcelData> getExcelModel(List<OverDueRateForMail> InfoList){
        List<ExcelData> excelDataList = new ArrayList<>();
        int count = 1;
        for (OverDueRateForMail info : InfoList) {
            count++;
            excelDataList.add(new ExcelData(count,1, ExcelDataType.STRING,info.getRefundDay()));
            excelDataList.add(new ExcelData(count,2, ExcelDataType.INTEGER,info.getNums()));
            excelDataList.add(new ExcelData(count,3, ExcelDataType.STRING,info.getD1()));
            excelDataList.add(new ExcelData(count,4, ExcelDataType.STRING,info.getD2()));
            excelDataList.add(new ExcelData(count,5, ExcelDataType.STRING,info.getD3()));
            excelDataList.add(new ExcelData(count,6, ExcelDataType.STRING,info.getD4()));
            excelDataList.add(new ExcelData(count,7, ExcelDataType.STRING,info.getD5()));
            excelDataList.add(new ExcelData(count,8, ExcelDataType.STRING,info.getD6()));
            excelDataList.add(new ExcelData(count,9, ExcelDataType.STRING,info.getD7()));
            excelDataList.add(new ExcelData(count,10, ExcelDataType.STRING,info.getD8()));
            excelDataList.add(new ExcelData(count,11, ExcelDataType.STRING,info.getD9()));
            excelDataList.add(new ExcelData(count,12, ExcelDataType.STRING,info.getD10()));
            excelDataList.add(new ExcelData(count,13, ExcelDataType.STRING,info.getD11()));
            excelDataList.add(new ExcelData(count,14, ExcelDataType.STRING,info.getD12()));
            excelDataList.add(new ExcelData(count,15, ExcelDataType.STRING,info.getD13()));
            excelDataList.add(new ExcelData(count,16, ExcelDataType.STRING,info.getD14()));
            excelDataList.add(new ExcelData(count,17, ExcelDataType.STRING,info.getD15()));
            excelDataList.add(new ExcelData(count,18, ExcelDataType.STRING,info.getD16()));
            excelDataList.add(new ExcelData(count,19, ExcelDataType.STRING,info.getD17()));
            excelDataList.add(new ExcelData(count,20, ExcelDataType.STRING,info.getD18()));
            excelDataList.add(new ExcelData(count,21, ExcelDataType.STRING,info.getD19()));
            excelDataList.add(new ExcelData(count,22, ExcelDataType.STRING,info.getD20()));
            excelDataList.add(new ExcelData(count,23, ExcelDataType.STRING,info.getD21()));
            excelDataList.add(new ExcelData(count,24, ExcelDataType.STRING,info.getD22()));
            excelDataList.add(new ExcelData(count,25, ExcelDataType.STRING,info.getD23()));
            excelDataList.add(new ExcelData(count,26, ExcelDataType.STRING,info.getD24()));
            excelDataList.add(new ExcelData(count,27, ExcelDataType.STRING,info.getD25()));
            excelDataList.add(new ExcelData(count,28, ExcelDataType.STRING,info.getD26()));
            excelDataList.add(new ExcelData(count,29, ExcelDataType.STRING,info.getD27()));
            excelDataList.add(new ExcelData(count,30, ExcelDataType.STRING,info.getD28()));
            excelDataList.add(new ExcelData(count,31, ExcelDataType.STRING,info.getD29()));
            excelDataList.add(new ExcelData(count,32, ExcelDataType.STRING,info.getD30()));
            excelDataList.add(new ExcelData(count,33, ExcelDataType.STRING,info.getD31()));
            excelDataList.add(new ExcelData(count,34, ExcelDataType.STRING,info.getD32()));
        }
        return excelDataList;
    }

    // 外呼号码Excel模板（新sql）
    private List<ExcelData> getCallNumberModel(List<DayCallNumber> InfoList){
        List<ExcelData> excelDataList = new ArrayList<>();
        int count = 1;
        for (DayCallNumber info : InfoList) {
            count++;
            excelDataList.add(new ExcelData(count,1, ExcelDataType.STRING,info.getMobileDes()));
            excelDataList.add(new ExcelData(count,2, ExcelDataType.STRING,info.getRefundDay()));
            excelDataList.add(new ExcelData(count,3, ExcelDataType.STRING,info.getUserType()));
            excelDataList.add(new ExcelData(count,4, ExcelDataType.STRING,info.getPhone()));
        }
        return excelDataList;
    }


    /**
     * Do-It 审核人员每日审核状况
     */
    public String doitDayReview() {
        StringBuffer sb = new StringBuffer();

//        // 表1.1标题：审核人员每日审核状况
//        sb.append("<h3>1.1 Do-It Check by auditor </h3>");
//        // 表1.1表格体
//        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
//        sb.append("<tr>")
//                .append("<th>").append("  createDay  ")
//                .append("<th>").append("  realname  ")
//                .append("<th>").append("  FC_Distribution  ")
//                .append("<th>").append("  FC_finish  ")
//                .append("<th>").append("  FC_finish_per  ")
//                .append("<th>").append("  FC_pass_per  ")
//                .append("<th>").append("  SC_Distribution  ")
//                .append("<th>").append("  SC_finish  ")
//                .append("<th>").append("  SC_finish_per  ")
//                .append("<th>").append("  SC_pass_per  ")
//        ;
//        List<DailyCheck> dayReviewList = reminderAndCheckDao
//                .getDayReview();
//        for (DailyCheck info : dayReviewList) {
//            sb.append("<tr>")
//                    .append("<td align=\"center\">").append(info.getCreateDay())
//                    .append("<td align=\"center\">").append(info.getRealname())
//                    .append("<td align=\"center\">").append(info.getFC_Distribution())
//                    .append("<td align=\"center\">").append(info.getFC_finish())
//                    .append("<td align=\"center\">").append(info.getFC_finish_per())
//                    .append("<td align=\"center\">").append(info.getFC_pass_per())
//                    .append("<td align=\"center\">").append(info.getSC_Distribution())
//                    .append("<td align=\"center\">").append(info.getSC_finish())
//                    .append("<td align=\"center\">").append(info.getSC_finish_per())
//                    .append("<td align=\"center\">").append(info.getSC_pass_per())
//            ;
//        }
//        sb.append("</table>");

        // 表1.1标题：每日初审数据
        sb.append("<h3>1.1 Do-It FC_Check by auditor </h3>");
        // 表1.1表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("  date  ")
                .append("<th>").append("  name  ")
                .append("<th>").append("  FC_Distribution  ")
                .append("<th>").append("  FC_finish  ")
                .append("<th>").append("  FC_finish_per  ")
                .append("<th>").append("  FC_pass_per  ")
        ;
        List<DailyCheck> dayReviewList = reminderAndCheckDao
                .getDayReviewFC();
        for (DailyCheck info : dayReviewList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getP1())
                    .append("<td align=\"center\">").append(info.getP2())
                    .append("<td align=\"center\">").append(info.getP3())
                    .append("<td align=\"center\">").append(info.getP4())
                    .append("<td align=\"center\">").append(info.getP5())
                    .append("<td align=\"center\">").append(info.getP6())
            ;
        }
        sb.append("</table>");

        // 表1.2标题：每日复审数据
        sb.append("<h3>1.2 Do-It SC_Check by auditor </h3>");
        // 表1.2表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("  date  ")
                .append("<th>").append("  name  ")
                .append("<th>").append("  SC_Distribution  ")
                .append("<th>").append("  SC_finish  ")
                .append("<th>").append("  SC_finish_per  ")
                .append("<th>").append("  SC_pass_per  ")
        ;
        List<DailyCheck> dayReviewList2 = reminderAndCheckDao
                .getDayReviewSC();
        for (DailyCheck info : dayReviewList2) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getP1())
                    .append("<td align=\"center\">").append(info.getP2())
                    .append("<td align=\"center\">").append(info.getP3())
                    .append("<td align=\"center\">").append(info.getP4())
                    .append("<td align=\"center\">").append(info.getP5())
                    .append("<td align=\"center\">").append(info.getP6())
            ;
        }
        sb.append("</table>");

        // 表1.3标题：初审
        sb.append("<h3>1.3 Waiting to FC check orders</h3>");
        // 表1.3表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("  ApplyDay  ")
                .append("<th>").append("  Distribution_WaitingFC  ")
                .append("<th>").append("  Undistribution_WaitingFC  ")
        ;
        List<DayReview2> dayReviewList3 = reminderAndCheckDao
                .getDayReview2();
        for (DayReview2 info : dayReviewList3) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getP1())
                    .append("<td align=\"center\">").append(info.getP2())
                    .append("<td align=\"center\">").append(info.getP3())
            ;
        }
        sb.append("</table>");

        // 表1.4标题：复审
        sb.append("<h3>1.4 Waiting to SC check orders</h3>");
        // 表1.4表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("  ApplyDay  ")
                .append("<th>").append("  Distribution_WaitingSC  ")
                .append("<th>").append("  Undistribution_WaitingSC  ")
        ;
        List<DayReview2> dayReviewList4 = reminderAndCheckDao
                .getDayReview3();
        for (DayReview2 info : dayReviewList4) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getP1())
                    .append("<td align=\"center\">").append(info.getP2())
                    .append("<td align=\"center\">").append(info.getP3())
            ;
        }
        sb.append("</table>");

        return sb.toString();
    }

    /**
     *  Do-It 初/复审人员通过率-D8逾期率差表格（从每个月1号到当天的统计结果）
     */
    public String doitDayReviewWeek() {
        StringBuffer sb = new StringBuffer();

        // 表1.1标题：Each SC auditor corresponds SC pass rate 每个复审人员对应的通过率
        sb.append("<h3>1.1 Each SC auditor corresponds SC pass rate </h3>");
        // 表1.1表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("  Month  ")
                .append("<th>").append("  SC_realname  ")
                .append("<th>").append("  SC_Finish  ")
                .append("<th>").append("  SC_Pass  ")
                .append("<th>").append("  SC_PassRate  ")
        ;
        List<DayReview> dayReviewList = reminderAndCheckDao
                .getReviewUser();
        for (DayReview info : dayReviewList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getP1())
                    .append("<td align=\"center\">").append(info.getP2())
                    .append("<td align=\"center\">").append(info.getP3())
                    .append("<td align=\"center\">").append(info.getP4())
                    .append("<td align=\"center\">").append(info.getP5())
            ;
        }
        sb.append("</table>");

        // 表1.2标题：Each FC auditor corresponds FC pass rate 每个初审人员对应的通过率
        sb.append("<h3>1.2 Each FC auditor corresponds FC pass rate </h3>");
        // 表1.2表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("  Month  ")
                .append("<th>").append("  FC_realname  ")
                .append("<th>").append("  FC_Finish  ")
                .append("<th>").append("  FC_Pass  ")
                .append("<th>").append("  FC_PassRate  ")
        ;
        List<DayReview> dayReviewList2 = reminderAndCheckDao
                .getReviewUser2();
        for (DayReview info : dayReviewList2) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getP1())
                    .append("<td align=\"center\">").append(info.getP2())
                    .append("<td align=\"center\">").append(info.getP3())
                    .append("<td align=\"center\">").append(info.getP4())
                    .append("<td align=\"center\">").append(info.getP5())
            ;
        }
        sb.append("</table>");

        // 表1.3标题：FC and SC total pass rate  初审和复审合计通过率
        sb.append("<h3>1.3 FC and SC total pass rate</h3>");
        // 表1.3表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("  Month  ")
                .append("<th>").append("  FC_Finish  ")
                .append("<th>").append("  FC_Pass  ")
                .append("<th>").append("  FC_PassRate  ")
                .append("<th>").append("  SC_Finish  ")
                .append("<th>").append("  SC_Pass  ")
                .append("<th>").append("  SC_PassRate  ")
        ;
        List<DayReview> dayReviewList3 = reminderAndCheckDao
                .getReviewUser3();
        for (DayReview info : dayReviewList3) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getP1())
                    .append("<td align=\"center\">").append(info.getP2())
                    .append("<td align=\"center\">").append(info.getP3())
                    .append("<td align=\"center\">").append(info.getP4())
                    .append("<td align=\"center\">").append(info.getP5())
                    .append("<td align=\"center\">").append(info.getP6())
                    .append("<td align=\"center\">").append(info.getP7())
            ;
        }
        sb.append("</table>");

        // 表1.4标题：Each SC auditor corresponds D8 difference With the average overdue rate  每个复审人员对应D8逾期率与平均预期率的差值，数值越小代表逾期率越低
        sb.append("<h3>1.4 Each SC auditor corresponds D8 difference With the average overdue rate/h3>");
        // 表1.4表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("  Month  ")
                .append("<th>").append("  SC_realname  ")
                .append("<th>").append("  D8_DueOrder  ")
                .append("<th>").append("  D8_OverdueOrder  ")
                .append("<th>").append("  Difference With Average Overdue Rate  ")
        ;
        List<DayReview> dayReviewList4 = reminderAndCheckDao
                .getReviewUser4();
        for (DayReview info : dayReviewList4) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getP1())
                    .append("<td align=\"center\">").append(info.getP2())
                    .append("<td align=\"center\">").append(info.getP3())
                    .append("<td align=\"center\">").append(info.getP4())
                    .append("<td align=\"center\">").append(info.getP5())
            ;
        }
        sb.append("</table>");

        // 表1.5标题：Each FC auditor corresponds D8 difference with the average overdue rate  每个初审人员对应D8逾期率与平均预期率的差值，数值越小代表逾期率越低
        sb.append("<h3>1.5 Each FC auditor corresponds D8 difference with the average overdue rate/h3>");
        // 表1.5表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("  Month  ")
                .append("<th>").append("  FC_realname  ")
                .append("<th>").append("  D8_DueOrder  ")
                .append("<th>").append("  D8_OverdueOrder  ")
                .append("<th>").append("  Difference With Average Overdue Rate  ")
        ;
        List<DayReview> dayReviewList5 = reminderAndCheckDao
                .getReviewUser5();
        for (DayReview info : dayReviewList5) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getP1())
                    .append("<td align=\"center\">").append(info.getP2())
                    .append("<td align=\"center\">").append(info.getP3())
                    .append("<td align=\"center\">").append(info.getP4())
                    .append("<td align=\"center\">").append(info.getP5())
            ;
        }
        sb.append("</table>");

        return sb.toString();
    }

    /**
     * Do-It 运营日报邮件
     */
    public String doitDayOperation(){

        StringBuffer sb = new StringBuffer();

        // 表1.1标题：每日新老户放款情况
        sb.append("<h3>1.1 每日新老户放款情况 </h3>");
        // 表1.1表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("放款日期")
                .append("<th>").append("总单量")
                .append("<th>").append("新户单量")
                .append("<th>").append("老户单量")
                .append("<th>").append("新老户比")
                .append("<th>").append("总放款")
                .append("<th>").append("新户放款总额")
                .append("<th>").append("老户放款总额")
                .append("<th>").append("分期订单")
                .append("<th>").append("分期订单金额")
                .append("<th>").append("展期订单")
                .append("<th>").append("展期订单金额")
        ;
        List<DayLoanStatus> dayLoanStatusList = reminderAndCheckDao
                .getDayLoanStatus();
        for (DayLoanStatus info : dayLoanStatusList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getDate())
                    .append("<td align=\"center\">").append(info.getOrders())
                    .append("<td align=\"center\">").append(info.getNewOrder())
                    .append("<td align=\"center\">").append(info.getOldOrder())
                    .append("<td align=\"center\">").append(info.getNor())
                    .append("<td align=\"center\">").append(info.getLendAmount())
                    .append("<td align=\"center\">").append(info.getNewAmount())
                    .append("<td align=\"center\">").append(info.getOldAmount())
                    .append("<td align=\"center\">").append(info.getBillOrders())
                    .append("<td align=\"center\">").append(info.getBillAmount())
                    .append("<td align=\"center\">").append(info.getExtension())
                    .append("<td align=\"center\">").append(info.getExtAmount())
            ;
        }
        sb.append("</table>");

        // 表1.2标题： 借款额度分布
        sb.append("<h3>1.2  借款额度分布 </h3>");
        // 表1.2表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("放款日期")
                .append("<th>").append("放款金额")
                .append("<th>").append("放款单量")
        .append("<th>").append("占比")
        .append("<th>").append("新户")
        .append("<th>").append("老户")
        .append("<th>").append("新老比")
        ;
        List<DayLoanAmout> dayLoanAmoutList = reminderAndCheckDao
                .getDayLoanAmout();
        for (DayLoanAmout info : dayLoanAmoutList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getLendDay())
                    .append("<td align=\"center\">").append(info.getAmountApply())
                    .append("<td align=\"center\">").append(info.getOrders())
                    .append("<td align=\"center\">").append(info.getRate())
                    .append("<td align=\"center\">").append(info.getNewOrder())
                    .append("<td align=\"center\">").append(info.getOldOrder())
                    .append("<td align=\"center\">").append(info.getNor())
            ;
        }
        sb.append("</table>");

        // 表1.3标题：新户转化率
        sb.append("<h3>1.3 新户转化率 </h3>");
        // 表1.3表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("进件日期")
                .append("<th>").append("进件数")
                .append("<th>").append("申请提交率")
                .append("<th>").append("机审通过率")
                .append("<th>").append("初审通过率")
                .append("<th>").append("复审通过率")
                .append("<th>").append("签章通过率")
                .append("<th>").append("成功放款率")
                .append("<th>").append("审核通过率")
                .append("<th>").append("提交放款率")
        ;
        List<NewUserRate> userRateList = reminderAndCheckDao
                .getNewUserRate();
        for (NewUserRate info : userRateList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getCreateDay())
                    .append("<td align=\"center\">").append(info.getApplyOrders())
                    .append("<td align=\"center\">").append(info.getSubmitRate())
                    .append("<td align=\"center\">").append(info.getMacPassRate())
                    .append("<td align=\"center\">").append(info.getJunPassRate())
                    .append("<td align=\"center\">").append(info.getSenPassRate())
                    .append("<td align=\"center\">").append(info.getSignPassRate())
                    .append("<td align=\"center\">").append(info.getSucLendRate())
                    .append("<td align=\"center\">").append(info.getExaPassRate())
                    .append("<td align=\"center\">").append(info.getSubLendRate())
            ;
        }
        sb.append("</table>");

        // 表1.4标题：安卓页面通过率
        sb.append("<h3>1.4 安卓页面通过率 </h3>");
        // 表1.4表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("渠道")
                .append("<th>").append("注册日期")
                .append("<th>").append("注册数")
                .append("<th>").append("注册申请率")
                .append("<th>").append("选择角色通过率")
                .append("<th>").append("填写身份信息通过率")
                .append("<th>").append("基本信息通过率")
                .append("<th>").append("工作或学校信息通过率")
                .append("<th>").append("联系人信息通过率")
                .append("<th>").append("银行卡通过率")
                .append("<th>").append("注册提交率")
        ;
        List<AndroidPageRate> androidPageRateList = reminderAndCheckDao
                .getAndroidPageRate();
        for (AndroidPageRate info : androidPageRateList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getChannel())
                    .append("<td align=\"center\">").append(info.getRegisterDay())
                    .append("<td align=\"center\">").append(info.getRegisterNum())
                    .append("<td align=\"center\">").append(info.getApplyRate())
                    .append("<td align=\"center\">").append(info.getRoleRate())
                    .append("<td align=\"center\">").append(info.getIdentityRate())
                    .append("<td align=\"center\">").append(info.getInformationRate())
                    .append("<td align=\"center\">").append(info.getWorkRate())
                    .append("<td align=\"center\">").append(info.getContactsRate())
                    .append("<td align=\"center\">").append(info.getBankRate())
                    .append("<td align=\"center\">").append(info.getSubmitRate())
            ;
        }
        sb.append("</table>");

        // 表2.1 标题：CashCash页面通过率
        sb.append("<h3>2.1 CashCash页面通过率 </h3>");
        // 表2.1 表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("渠道")
                .append("<th>").append("注册日期")
                .append("<th>").append("注册数")
                .append("<th>").append("注册申请率")
                .append("<th>").append("选择角色通过率")
                .append("<th>").append("填写身份信息通过率")
                .append("<th>").append("基本信息通过率")
                .append("<th>").append("工作或学校信息通过率")
                .append("<th>").append("联系人信息通过率")
                .append("<th>").append("银行卡通过率")
                .append("<th>").append("注册提交率")
        ;
        List<AndroidPageRate> cash2PageRateList = reminderAndCheckDao
                .getCash2PageRate();
        for (AndroidPageRate info : cash2PageRateList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getChannel())
                    .append("<td align=\"center\">").append(info.getRegisterDay())
                    .append("<td align=\"center\">").append(info.getRegisterNum())
                    .append("<td align=\"center\">").append(info.getApplyRate())
                    .append("<td align=\"center\">").append(info.getRoleRate())
                    .append("<td align=\"center\">").append(info.getIdentityRate())
                    .append("<td align=\"center\">").append(info.getInformationRate())
                    .append("<td align=\"center\">").append(info.getWorkRate())
                    .append("<td align=\"center\">").append(info.getContactsRate())
                    .append("<td align=\"center\">").append(info.getBankRate())
                    .append("<td align=\"center\">").append(info.getSubmitRate())
            ;
        }
        sb.append("</table>");

        // 表2.2 标题：CashCash获客成本
        sb.append("<h3>2.2 CashCash获客成本 </h3>");
        // 表2.2 表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("进件日期")
                .append("<th>").append("新户进件数")
                .append("<th>").append("新户提交数")
                .append("<th>").append("新户放款数")
                .append("<th>").append("新户成本")
                .append("<th>").append("新户进件提交率")
                .append("<th>").append("新户提交放款率")
                .append("<th>").append("老户进件数")
                .append("<th>").append("老户放款数")
                .append("<th>").append("总进件数")
        ;
        List<Cash2Cost> cash2CostList = reminderAndCheckDao
                .getCash2Cost();
        for (Cash2Cost info : cash2CostList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getCreateDay())
                    .append("<td align=\"center\">").append(info.getNewOrders())
                    .append("<td align=\"center\">").append(info.getNewSubmit())
                    .append("<td align=\"center\">").append(info.getSucNew())
                    .append("<td align=\"center\">").append(info.getNewCost())
                    .append("<td align=\"center\">").append(info.getNewSubRate())
                    .append("<td align=\"center\">").append(info.getNewLendRate())
                    .append("<td align=\"center\">").append(info.getOldOrders())
                    .append("<td align=\"center\">").append(info.getSucOld())
                    .append("<td align=\"center\">").append(info.getTotalOrders())
            ;
        }
        sb.append("</table>");

        // 表2.3 标题：CashCash转自有
        sb.append("<h3>2.3 CashCash转自有 </h3>");
        // 表2.3 表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("还款日期")
                .append("<th>").append("还款数")
                .append("<th>").append("复借数")
                .append("<th>").append("复借率")
                .append("<th>").append("CashCash数")
                .append("<th>").append("自有数")
                .append("<th>").append("转自有率")
        ;
        List<Cash2TransferRate> cash2TransferRateList = reminderAndCheckDao
                .getCash2TransferRate();
        for (Cash2TransferRate info : cash2TransferRateList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getRepayDay())
                    .append("<td align=\"center\">").append(info.getRepayOrder())
                    .append("<td align=\"center\">").append(info.getAgainBorrow())
                    .append("<td align=\"center\">").append(info.getAgainBorRate())
                    .append("<td align=\"center\">").append(info.getCashBorrow())
                    .append("<td align=\"center\">").append(info.getOwnBorrow())
                    .append("<td align=\"center\">").append(info.getTranOwnRate())
            ;
        }
        sb.append("</table>");

        // 表3.1 标题：用户借款次数分布
        sb.append("<h3>3.1 用户借款次数分布 </h3>");
        // 表3.1 表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("创建时间")
                .append("<th>").append("借款次数")
                .append("<th>").append("用户数")
                .append("<th>").append("在借数")
                .append("<th>").append("逾期8天以上")
                .append("<th>").append("沉默数")
        ;
        List<DayLoanCount> dayLoanCountList = reminderAndCheckDao
                .getDayLoanCount();
        for (DayLoanCount info : dayLoanCountList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getCreatetime())
                    .append("<td align=\"center\">").append(info.getTimes())
                    .append("<td align=\"center\">").append(info.getUsers())
                    .append("<td align=\"center\">").append(info.getBoringUser())
                    .append("<td align=\"center\">").append(info.getOverD8())
                    .append("<td align=\"center\">").append(info.getSilence())
            ;
        }
        sb.append("</table>");

        // 表3.2 标题：当日复借率
        sb.append("<h3>3.2 当日复借率 </h3>");
        // 表3.2 表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("日期")
                .append("<th>").append("当日还")
                .append("<th>").append("当日借")
                .append("<th>").append("占比")
        ;
        List<DayReborrowRate> dayReborrowRateList = reminderAndCheckDao
                .getDayReborrowRate();
        for (DayReborrowRate info : dayReborrowRateList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getRepayDay())
                    .append("<td align=\"center\">").append(info.getToRepay())
                    .append("<td align=\"center\">").append(info.getToLend())
                    .append("<td align=\"center\">").append(info.getRate())
            ;
        }
        sb.append("</table>");

        // 表3.3 标题：用户沉默时长分布
        sb.append("<h3>3.3 用户沉默时长分布 </h3>");
        // 表3.3 表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("创建时间")
                .append("<th>").append("沉默时长")
                .append("<th>").append("沉默用户数")
        ;
        List<DayLoanSilence> dayLoanSilenceList = reminderAndCheckDao
                .getDayLoanSilence();
        for (DayLoanSilence info : dayLoanSilenceList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getCreatetime())
                    .append("<td align=\"center\">").append(info.getTimeLength())
                    .append("<td align=\"center\">").append(info.getSilenceUser())
            ;
        }
        sb.append("</table>");

        // 表3.4 标题：用户在借率
        sb.append("<h3>3.4 用户在借率 </h3>");
        // 表3.4 表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("创建时间")
                .append("<th>").append("放款成功用户")
                .append("<th>").append("失去资格用户")
                .append("<th>").append("可借款用户")
                .append("<th>").append("借款中用户")
                .append("<th>").append("在借率")
        ;
        List<DayLoanInRate> dayLoanInRateList = reminderAndCheckDao
                .getDayLoanInRate();
        for (DayLoanInRate info : dayLoanInRateList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getCreatetime())
                    .append("<td align=\"center\">").append(info.getLendUser())
                    .append("<td align=\"center\">").append(info.getUnableUser())
                    .append("<td align=\"center\">").append(info.getGreenUser())
                    .append("<td align=\"center\">").append(info.getBorUser())
                    .append("<td align=\"center\">").append(info.getBorRate())
            ;
        }
        sb.append("</table>");

        // 表4.1 标题：到期订单
        sb.append("<h3>4.1  到期订单 </h3>");
        // 表4.1  表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("日期")
                .append("<th>").append("到期订单")
                .append("<th>").append("新户订单")
                .append("<th>").append("老户常规订单")
                .append("<th>").append("分期订单")
                .append("<th>").append("展期订单")
                .append("<th>").append("总到期金额")
                .append("<th>").append("新户到期金额")
                .append("<th>").append("老户到期金额")
                .append("<th>").append("分期到期金额")
                .append("<th>").append("展期到期金额")
        ;
        List<ExpireDateOrder> expireDateOrderList = reminderAndCheckDao
                .getExpireDateOrder();
        for (ExpireDateOrder info : expireDateOrderList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getDueDay())
                    .append("<td align=\"center\">").append(info.getOrders())
                    .append("<td align=\"center\">").append(info.getNewOrder())
                    .append("<td align=\"center\">").append(info.getOldOrder())
                    .append("<td align=\"center\">").append(info.getBillOrders())
                    .append("<td align=\"center\">").append(info.getExtension())
                    .append("<td align=\"center\">").append(info.getDueAmount())
                    .append("<td align=\"center\">").append(info.getNewAmount())
                    .append("<td align=\"center\">").append(info.getOldAmount())
                    .append("<td align=\"center\">").append(info.getBillAmount())
                    .append("<td align=\"center\">").append(info.getExtAmount())
            ;
        }
        sb.append("</table>");

        // 表4.2 标题：Twilio外呼接通率
        sb.append("<h3>4.2  Twilio外呼接通率 </h3>");
        // 表4.2  表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("外呼日期")
                .append("<th>").append("外呼阶段")
                .append("<th>").append("外呼次数")
                .append("<th>").append("接通次数")
                .append("<th>").append("接通率")
                .append("<th>").append("无人接听率")
                .append("<th>").append("失败率")
                .append("<th>").append("繁忙率")
                .append("<th>").append("外呼花费")
        ;
        List<TwilioRate> twilioRateList = reminderAndCheckDao
                .getTwilioRate();
        for (TwilioRate info : twilioRateList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getDate())
                    .append("<td align=\"center\">").append(info.getCallPhase())
                    .append("<td align=\"center\">").append(info.getTotalCall())
                    .append("<td align=\"center\">").append(info.getConnectCall())
                    .append("<td align=\"center\">").append(info.getConnectRate())
                    .append("<td align=\"center\">").append(info.getNoAnswerRate())
                    .append("<td align=\"center\">").append(info.getFailedRate())
                    .append("<td align=\"center\">").append(info.getBusyRate())
                    .append("<td align=\"center\">").append(info.getCallCost())
            ;
        }
        sb.append("</table>");

        return sb.toString();
    }


    /**
     * Do-It 催收时报
     */
    public String doitCollectionMail(){

        StringBuffer sb = new StringBuffer();

        // 表1.1标题：整点首逾监控
        sb.append("<h3>1.1 整点首逾监控(overall first pass monitoring) </h3>");
        // 表1.1表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("产品(produk)")
                .append("<th>").append("订单(pesanan)")
                .append("<th>").append(" H09 ")
                .append("<th>").append(" H10 ")
                .append("<th>").append(" H11 ")
                .append("<th>").append(" H12 ")
                .append("<th>").append(" H13 ")
                .append("<th>").append(" H14 ")
                .append("<th>").append(" H15 ")
                .append("<th>").append(" H16 ")
                .append("<th>").append(" H17 ")
                .append("<th>").append(" H18 ")
                .append("<th>").append(" H19 ")
                .append("<th>").append(" H20 ")
                .append("<th>").append(" H21 ")
        ;
        List<MonitoringData> monitoringDataList = ordDao
                .getMonitoringData();
        for (MonitoringData info : monitoringDataList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getProduct())
                    .append("<td align=\"center\">").append(info.getOrders())
                    .append("<td align=\"center\">").append(info.getH09())
                    .append("<td align=\"center\">").append(info.getH10())
                    .append("<td align=\"center\">").append(info.getH11())
                    .append("<td align=\"center\">").append(info.getH12())
                    .append("<td align=\"center\">").append(info.getH13())
                    .append("<td align=\"center\">").append(info.getH14())
                    .append("<td align=\"center\">").append(info.getH15())
                    .append("<td align=\"center\">").append(info.getH16())
                    .append("<td align=\"center\">").append(info.getH17())
                    .append("<td align=\"center\">").append(info.getH18())
                    .append("<td align=\"center\">").append(info.getH19())
                    .append("<td align=\"center\">").append(info.getH20())
                    .append("<td align=\"center\">").append(info.getH21())
            ;
        }
        sb.append("</table>");

        // 表2.1标题：内催D0分组催收情况
        sb.append("<h3>2.1 内催D0分组催收情况(internal reminder kondisi collection team D0) </h3>");
        // 表2.1表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("日期(tanggal)")
                .append("<th>").append("催收员(collector)")
                .append("<th>").append("组长(TL)")
                .append("<th>").append("分案数(jumlah kasus yg dibagi)")
                .append("<th>").append("回收数(jumlah pengembalian)")
                .append("<th>").append("回收率(persentase pengembalian)")
        ;
        List<D0CollectionData> D0CollectionDataList = ordDao
                .getD0CollectionData();
        for (D0CollectionData info : D0CollectionDataList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getDate())
                    .append("<td align=\"center\">").append(info.getRealName())
                    .append("<td align=\"center\">").append(info.getParentName())
                    .append("<td align=\"center\">").append(info.getTaskNum())
                    .append("<td align=\"center\">").append(info.getRecoveryNum())
                    .append("<td align=\"center\">").append(info.getRatio())
            ;
        }
        sb.append("</table>");

        // 表2.2标题：内催D1-2分组催收情况
        sb.append("<h3>2.2 内催D1-2分组催收情况(internal reminder kondisi collection team D1-2) </h3>");
        // 表2.2表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("日期(tanggal)")
                .append("<th>").append("催收员(collector)")
                .append("<th>").append("组长(TL)")
                .append("<th>").append("分案数(jumlah kasus yg dibagi)")
                .append("<th>").append("回收数(jumlah pengembalian)")
                .append("<th>").append("回收率(persentase pengembalian)")
                .append("<th>").append("今日回收数(jumlah pengembalian hari ini)")
        ;
        List<D1AndD2CollectionData> D1AndD2CollectionDataList = ordDao
                .getD1AndD2CollectionData();
        for (D1AndD2CollectionData info : D1AndD2CollectionDataList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getDate())
                    .append("<td align=\"center\">").append(info.getRealName())
                    .append("<td align=\"center\">").append(info.getParentName())
                    .append("<td align=\"center\">").append(info.getTaskNum())
                    .append("<td align=\"center\">").append(info.getRecoveryNum())
                    .append("<td align=\"center\">").append(info.getRatio())
                    .append("<td align=\"center\">").append(info.getTodayNum())
            ;
        }
        sb.append("</table>");

        // 表2.3标题：内催D3-7分组催收情况
        sb.append("<h3>2.3 内催D3-7分组催收情况(internal reminder kondisi collection team D3-7) </h3>");
        // 表2.3表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("日期(tanggal)")
                .append("<th>").append("催收员(collector)")
                .append("<th>").append("组长(TL)")
                .append("<th>").append("分案数(jumlah kasus yg dibagi)")
                .append("<th>").append("回收数(jumlah pengembalian)")
                .append("<th>").append("回收率(persentase pengembalian)")
                .append("<th>").append("今日回收数(jumlah pengembalian hari ini)")
        ;
        List<D1AndD2CollectionData> D3ToD7CollectionDataList = ordDao
                .getD3ToD7CollectionData();
        for (D1AndD2CollectionData info : D3ToD7CollectionDataList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getDate())
                    .append("<td align=\"center\">").append(info.getRealName())
                    .append("<td align=\"center\">").append(info.getParentName())
                    .append("<td align=\"center\">").append(info.getTaskNum())
                    .append("<td align=\"center\">").append(info.getRecoveryNum())
                    .append("<td align=\"center\">").append(info.getRatio())
                    .append("<td align=\"center\">").append(info.getTodayNum())
            ;
        }
        sb.append("</table>");

        // 表2.4标题：内催D31-D60分组催收情况
        sb.append("<h3>2.4 内催D31-D60分组催收情况(internal reminder kondisi collection team D31-D60) </h3>");
        // 表2.4表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("催收员(collector)")
                .append("<th>").append("今日分案(jumlah kasus yg dibagi hari ini)")
                .append("<th>").append("分案数(jumlah kasus yg dibagi)")
                .append("<th>").append("回收数(jumlah pengembalian)")
                .append("<th>").append("回收率(persentase pengembalian)")
                .append("<th>").append("今日回收数(jumlah pengembalian hari ini)")
        ;
        List<D31ToD60CollectionData> D31ToD60CollectionDataList = ordDao
                .getD31ToD60CollectionData();
        for (D31ToD60CollectionData info : D31ToD60CollectionDataList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getRealName())
                    .append("<td align=\"center\">").append(info.getTaskToday())
                    .append("<td align=\"center\">").append(info.getTaskNum())
                    .append("<td align=\"center\">").append(info.getRecoveryNum())
                    .append("<td align=\"center\">").append(info.getRatio())
                    .append("<td align=\"center\">").append(info.getTodayNum())
            ;
        }
        sb.append("</table>");

        // 表3.1标题：勤为D1-2分组催收情况
        sb.append("<h3>3.1 勤为D1-2分组催收情况(kondisi collection QinWei D1-2) </h3>");
        // 表3.1表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("日期(tanggal)")
                .append("<th>").append("催收员(collector)")
                .append("<th>").append("分案数(jumlah kasus yg dibagi)")
                .append("<th>").append("回收数(jumlah pengembalian)")
                .append("<th>").append("回收率(persentase pengembalian)")
                .append("<th>").append("今日回收数(jumlah pengembalian hari ini)")
        ;
        List<QWD1AndD2CollectionData> QWD1AndD2CollectionDataList = ordDao
                .getQWD1AndD2CollectionData();
        for (QWD1AndD2CollectionData info : QWD1AndD2CollectionDataList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getDate())
                    .append("<td align=\"center\">").append(info.getRealName())
                    .append("<td align=\"center\">").append(info.getTaskNum())
                    .append("<td align=\"center\">").append(info.getRecoveryNum())
                    .append("<td align=\"center\">").append(info.getRatio())
                    .append("<td align=\"center\">").append(info.getTodayNum())
            ;
        }
        sb.append("</table>");

        // 表3.2标题：勤为D3-7分组催收情况
        sb.append("<h3>3.2 勤为D3-7分组催收情况(kondisi collection QinWei D3-7) </h3>");
        // 表3.2表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("日期(tanggal)")
                .append("<th>").append("催收员(collector)")
                .append("<th>").append("分案数(jumlah kasus yg dibagi)")
                .append("<th>").append("回收数(jumlah pengembalian)")
                .append("<th>").append("回收率(persentase pengembalian)")
                .append("<th>").append("今日回收数(jumlah pengembalian hari ini)")
        ;
        List<QWD1AndD2CollectionData> QWD3ToD7CollectionDataList = ordDao
                .getQWD3ToD7CollectionData();
        for (QWD1AndD2CollectionData info : QWD3ToD7CollectionDataList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getDate())
                    .append("<td align=\"center\">").append(info.getRealName())
                    .append("<td align=\"center\">").append(info.getTaskNum())
                    .append("<td align=\"center\">").append(info.getRecoveryNum())
                    .append("<td align=\"center\">").append(info.getRatio())
                    .append("<td align=\"center\">").append(info.getTodayNum())
            ;
        }
        sb.append("</table>");

        // 表3.3、 本月勤为D8-30分组催收情况
        sb.append("<h3>3.3 本月勤为D8-30分组催收情况(kondisi collection QinWei D8-30 bulan ini) </h3>");
        // 表3.3表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("催收员(collector)")
                .append("<th>").append("今日分案(jumlah kasus yg dibagi hari ini)")
                .append("<th>").append("分案数(jumlah kasus yg dibagi)")
                .append("<th>").append("回收数(jumlah pengembalian)")
                .append("<th>").append("回收率(persentase pengembalian)")
                .append("<th>").append("今日回收数(jumlah pengembalian hari ini)")
        ;
        List<QWD8ToD30ThisMonthCollectionData> QWD8ToD30ThisMonthCollectionDataList = ordDao
                .getQWD8ToD30ThisMonthCollectionData();
        for (QWD8ToD30ThisMonthCollectionData info : QWD8ToD30ThisMonthCollectionDataList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getRealName())
                    .append("<td align=\"center\">").append(info.getTaskToday())
                    .append("<td align=\"center\">").append(info.getTaskNum())
                    .append("<td align=\"center\">").append(info.getRecoveryNum())
                    .append("<td align=\"center\">").append(info.getRatio())
                    .append("<td align=\"center\">").append(info.getTodayNum())
            ;
        }
        sb.append("</table>");

        // 表3.4、 上月勤为D8-30分组催收情况
        sb.append("<h3>3.4 上月勤为D8-30分组催收情况(kondisi collection QinWei tim D8-30 bulan lalu) </h3>");
        // 表3.4表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("催收员(collector)")
                .append("<th>").append("今日分案(jumlah kasus yg dibagi hari ini)")
                .append("<th>").append("分案数(jumlah kasus yg dibagi)")
                .append("<th>").append("回收数(jumlah pengembalian)")
                .append("<th>").append("回收率(persentase pengembalian)")
                .append("<th>").append("今日回收数(jumlah pengembalian hari ini)")
        ;
        List<QWD8ToD30ThisMonthCollectionData> QWD8ToD30LastMonthCollectionDataList = ordDao
                .getQWD8ToD30LastMonthCollectionData();
        for (QWD8ToD30ThisMonthCollectionData info : QWD8ToD30LastMonthCollectionDataList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getRealName())
                    .append("<td align=\"center\">").append(info.getTaskToday())
                    .append("<td align=\"center\">").append(info.getTaskNum())
                    .append("<td align=\"center\">").append(info.getRecoveryNum())
                    .append("<td align=\"center\">").append(info.getRatio())
                    .append("<td align=\"center\">").append(info.getTodayNum())
            ;
        }
        sb.append("</table>");

        // 表4.1、 本月 QUIROS D8-30分组催收情况
        sb.append("<h3>4.1 本月 QUIROS D8-30分组催收情况(kondisi collection QUIROS D8-30 bulan ini) </h3>");
        // 表4.1表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("催收员(collector)")
                .append("<th>").append("今日分案(jumlah kasus yg dibagi hari ini)")
                .append("<th>").append("分案数(jumlah kasus yg dibagi)")
                .append("<th>").append("回收数(jumlah pengembalian)")
                .append("<th>").append("回收率(persentase pengembalian)")
                .append("<th>").append("今日回收数(jumlah pengembalian hari ini)")
        ;
        List<QWD8ToD30ThisMonthCollectionData> QUIROSD8ToD30ThisMonthCollectionDataList = ordDao
                .getQUIROSD8ToD30ThisMonthCollectionData();
        for (QWD8ToD30ThisMonthCollectionData info : QUIROSD8ToD30ThisMonthCollectionDataList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getRealName())
                    .append("<td align=\"center\">").append(info.getTaskToday())
                    .append("<td align=\"center\">").append(info.getTaskNum())
                    .append("<td align=\"center\">").append(info.getRecoveryNum())
                    .append("<td align=\"center\">").append(info.getRatio())
                    .append("<td align=\"center\">").append(info.getTodayNum())
            ;
        }
        sb.append("</table>");

        // 表4.2、 上月 QUIROS D8-30分组催收情况
        sb.append("<h3>4.2 上月 QUIROS D8-30分组催收情况(kondisi collection QUIROS tim D8-30 bulan lalu) </h3>");
        // 表4.2表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("催收员(collector)")
                .append("<th>").append("今日分案(jumlah kasus yg dibagi hari ini)")
                .append("<th>").append("分案数(jumlah kasus yg dibagi)")
                .append("<th>").append("回收数(jumlah pengembalian)")
                .append("<th>").append("回收率(persentase pengembalian)")
                .append("<th>").append("今日回收数(jumlah pengembalian hari ini)")
        ;
        List<QWD8ToD30ThisMonthCollectionData> QUIROSD8ToD30LastMonthCollectionDataList = ordDao
                .getQUIROSD8ToD30LastMonthCollectionData();
        for (QWD8ToD30ThisMonthCollectionData info : QUIROSD8ToD30LastMonthCollectionDataList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getRealName())
                    .append("<td align=\"center\">").append(info.getTaskToday())
                    .append("<td align=\"center\">").append(info.getTaskNum())
                    .append("<td align=\"center\">").append(info.getRecoveryNum())
                    .append("<td align=\"center\">").append(info.getRatio())
                    .append("<td align=\"center\">").append(info.getTodayNum())
            ;
        }
        sb.append("</table>");

        return sb.toString();
    }


    /**
     * Do-It 催收质检
     */
    public String doitCollectionQIMail() {

        StringBuffer sb = new StringBuffer();

        // 表1 标题：D0_D1-2_D3-7新增未还款订单处理情况
        sb.append("<h3>1. D0_D1-2_D3-7新增未还款订单处理情况/ Kondisi perkembangan order belum bayar D0_D1-2_D3-7 </h3>");
        // 表1 表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("阶段/ Tahapan")
                .append("<th>").append("日期/ Tanggal")
                .append("<th>").append("组长/ TL")
                .append("<th>").append("催收员/ Collector")
                .append("<th>").append("未还款单数/ Data belum bayar")
                .append("<th>").append("WA或电话处理过单数/ Data sdh telepon dan WA")
                .append("<th>").append("未还款未处理单数/ Data belum bayar belum diproses")
        ;
        List<D0ToD7OrderCollectionData> D0ToD7OrderCollectionDataList = collectionOrderHistoryDao
                .getD0ToD7OrderCollectionData();
        for (D0ToD7OrderCollectionData info : D0ToD7OrderCollectionDataList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getStage())
                    .append("<td align=\"center\">").append(info.getCreateDay())
                    .append("<td align=\"center\">").append(info.getParentName())
                    .append("<td align=\"center\">").append(info.getStudentName())
                    .append("<td align=\"center\">").append(info.getTaskNum())
                    .append("<td align=\"center\">").append(info.getDealNum())
                    .append("<td align=\"center\">").append(info.getNotdeal())
            ;
        }
        sb.append("</table>");

        // 表2 标题：D0_D1-2_D3-7当日承诺还款时间前后半小时未跟进(新增+存量)
        sb.append("<h3>2. D0_D1-2_D3-7当日承诺还款时间前后半小时未跟进(新增+存量)/ D0_D1-2_D3-7 janji bayar hari itu tapi belum FU 30 menit sebelum dan sesudah jam yang dijanjikan (tambahan + telah ada sebelumnya) </h3>");
        // 表2 表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("阶段/ Tahapan")
                .append("<th>").append("日期/ Tanggal")
                .append("<th>").append("组长/ TL")
                .append("<th>").append("催收员/ Collector")
                .append("<th>").append("承诺还款未跟进/ Sdh janji bayar belum FU")
        ;
        List<D0ToD7OrderNotFollowUpCollectionData> D0ToD7OrderNotFollowUpCollectionDataList = collectionOrderHistoryDao
                .getD0ToD7OrderNotFollowUpCollectionData();
        for (D0ToD7OrderNotFollowUpCollectionData info : D0ToD7OrderNotFollowUpCollectionDataList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getStage())
                    .append("<td align=\"center\">").append(info.getCreateDay())
                    .append("<td align=\"center\">").append(info.getParentName())
                    .append("<td align=\"center\">").append(info.getStudentName())
                    .append("<td align=\"center\">").append(info.getNotFollowed())
            ;
        }
        sb.append("</table>");

        // 表3 标题：D1-2_D3-7 新增未还款催收情况
        sb.append("<h3>3. D1-2_D3-7 新增未还款催收情况/D1-2_D3-7 kondisi collection tambahan yang belum bayar </h3>");
        // 表3 表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("阶段/ Tahapan")
                .append("<th>").append("日期/ Tanggal")
                .append("<th>").append("组长/ TL")
                .append("<th>").append("催收员/ Collector")
                .append("<th>").append("新增未还款单数/ Data tambahan belum bayar")
                .append("<th>").append("联系本人单数/ jumlah yg dihubungin ke orang langsung")
                .append("<th>").append("未联系本人单数/ jumlah yg belum dihubungin ke orang langsung")
                .append("<th>").append("联系1-3个紧急联系人的单数/ Data yang menghubungi 1-3 orang kontak darurat")
                .append("<th>").append("联系4个紧急联系人单数/ Data yang menghubungi 4 orang kontak darurat")
                .append("<th>").append("只联系本人单数/ Data hanya menghubungi nasabah")
        ;
        List<D1ToD7OldOrderNotCallCollectionData> D1ToD7NewOrderNotCallCollectionDataList = collectionOrderHistoryDao
                .getD1ToD7NewOrderNotCallCollectionData();
        for (D1ToD7OldOrderNotCallCollectionData info : D1ToD7NewOrderNotCallCollectionDataList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getStage())
                    .append("<td align=\"center\">").append(info.getCreateDay())
                    .append("<td align=\"center\">").append(info.getParentName())
                    .append("<td align=\"center\">").append(info.getStudentName())
                    .append("<td align=\"center\">").append(info.getTaskNum())
                    .append("<td align=\"center\">").append(info.getContactUser())
                    .append("<td align=\"center\">").append(info.getNotContactUser())
                    .append("<td align=\"center\">").append(info.getCall3())
                    .append("<td align=\"center\">").append(info.getAllCall())
                    .append("<td align=\"center\">").append(info.getOnlyself())
            ;
        }
        sb.append("</table>");

        // 表4 标题：D1-2_D3-7 存量未还款催收情况
        sb.append("<h3>4. D1-2_D3-7 存量未还款催收情况/ kolektor D1-2 dan D3-7 dari data yang ada, belum mengembalikan </h3>");
        // 表4 表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("阶段/ Tahapan")
                .append("<th>").append("日期/ Tanggal")
                .append("<th>").append("组长/ TL")
                .append("<th>").append("催收员/ Collector")
                .append("<th>").append("新增未还款单数/ Data tambahan belum bayar")
                .append("<th>").append("联系本人单数/ jumlah yg dihubungin ke orang langsung")
                .append("<th>").append("未联系本人单数/ jumlah yg belum dihubungin ke orang langsung")
                .append("<th>").append("联系1-3个紧急联系人的单数/ Data yang menghubungi 1-3 orang kontak darurat")
                .append("<th>").append("联系4个紧急联系人单数/ Data yang menghubungi 4 orang kontak darurat")
                .append("<th>").append("只联系本人单数/ Data hanya menghubungi nasabah")
        ;
        List<D1ToD7NewOrderNotCallCollectionData> D1ToD7OldOrderNotCallCollectionDataList = collectionOrderHistoryDao
                .getD1ToD7OldOrderNotCallCollectionData();
        for (D1ToD7NewOrderNotCallCollectionData info : D1ToD7OldOrderNotCallCollectionDataList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getStep())
                    .append("<td align=\"center\">").append(info.getCreateDay())
                    .append("<td align=\"center\">").append(info.getParentName())
                    .append("<td align=\"center\">").append(info.getStudentName())
                    .append("<td align=\"center\">").append(info.getTaskNum())
                    .append("<td align=\"center\">").append(info.getContactUser())
                    .append("<td align=\"center\">").append(info.getNotContactUser())
                    .append("<td align=\"center\">").append(info.getCall3())
                    .append("<td align=\"center\">").append(info.getAllCall())
                    .append("<td align=\"center\">").append(info.getOnlyself())
            ;
        }
        sb.append("</table>");

        // 表5 标题：D0当日未还款订单WA和电话是否全部标亮
        sb.append("<h3>5. D0当日未还款订单WA和电话是否全部标亮/ D0 Apakah pesanan hari itu belum bayar  WA dan teleponnya semua dihighlight </h3>");
        // 表5 表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("阶段/ Tahapan")
                .append("<th>").append("分案时间/ Waktu bagi data")
                .append("<th>").append("组长/ TL")
                .append("<th>").append("催收员/ Collector")
                .append("<th>").append("未还款单数/ Data belum bayar")
                .append("<th>").append("处理过单量/ Data yang sdh diproses")
                .append("<th>").append("未还款且未跟进WA或者电话/ Belum bayar dan belum FU WA atau telepon")
                .append("<th>").append("用过WA和电话单量/ Sdh WA dan telepon")
        ;
        List<D0OrderCallCollectionData> D0OrderCallCollectionDataList = collectionOrderHistoryDao
                .getD0OrderCallCollectionData();
        for (D0OrderCallCollectionData info : D0OrderCallCollectionDataList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getStage())
                    .append("<td align=\"center\">").append(info.getCreateDay())
                    .append("<td align=\"center\">").append(info.getParentName())
                    .append("<td align=\"center\">").append(info.getStudentName())
                    .append("<td align=\"center\">").append(info.getTaskNum())
                    .append("<td align=\"center\">").append(info.getDealNum())
                    .append("<td align=\"center\">").append(info.getNotDealNum())
                    .append("<td align=\"center\">").append(info.getAllMode())
            ;
        }
        sb.append("</table>");

        // 表6 标题：D1-2_D3-7逾期订单是否全部跟进(新增+存量)
        sb.append("<h3>6. D1-2_D3-7逾期订单是否全部跟进(新增+存量)/ D1-2_D3-7 Apakah order yang terlambat sudah semua di FU (tambahan + telah ada sebelumnya) </h3>");
        // 表6 表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("阶段/ Tahapan")
                .append("<th>").append("日期/ Tanggal")
                .append("<th>").append("组长/ TL")
                .append("<th>").append("催收员/ Collector")
                .append("<th>").append("未还款单数/ Data belum bayar")
                .append("<th>").append("未还款处理过单量/ Data belum bayar tapi sdh pernah diproses")
                .append("<th>").append("未还款未处理过单量/ Data belum bayar dan belum diproses")
        ;
        List<D1ToD7OverdueOrderCollectionData> D1ToD7OverdueOrderCollectionDataList = collectionOrderHistoryDao
                .getD1ToD7OverdueOrderCollectionData();
        for (D1ToD7OverdueOrderCollectionData info : D1ToD7OverdueOrderCollectionDataList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getStage())
                    .append("<td align=\"center\">").append(info.getCreateDay())
                    .append("<td align=\"center\">").append(info.getParentName())
                    .append("<td align=\"center\">").append(info.getStudentName())
                    .append("<td align=\"center\">").append(info.getTaskNum())
                    .append("<td align=\"center\">").append(info.getLinkNum())
                    .append("<td align=\"center\">").append(info.getNotLinkNum())
            ;
        }
        sb.append("</table>");

        // 表7 标题：D0_D1-2_D3-7新增未还款未处理订单明细
        sb.append("<h3>7.D0_D1-2_D3-7新增未还款未处理订单明细/ D0_D1-2_D3-7 Detail order tambahan yang belum diproses belum membayar </h3>");
        // 表7 表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("阶段/ Tahapan")
                .append("<th>").append("日期/ Tanggal")
                .append("<th>").append("组长/ TL")
                .append("<th>").append("催收员/ Collector")
                .append("<th>").append("订单编号/ Order ID")
        ;
        List<D0ToD7PendingOrderDetails> D0ToD7PendingOrderDetailsList = collectionOrderHistoryDao
                .getD0ToD7PendingOrderDetails();
        for (D0ToD7PendingOrderDetails info : D0ToD7PendingOrderDetailsList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getStep())
                    .append("<td align=\"center\">").append(info.getCreateDay())
                    .append("<td align=\"center\">").append(info.getParentName())
                    .append("<td align=\"center\">").append(info.getStudentName())
                    .append("<td align=\"center\">").append(info.getOrderUUID())
            ;
        }

        sb.append("</table>");

        // 表8 标题：D0_D1-2_D3-7可联承诺前后半小时未处理订单明细
        sb.append("<h3>8.D0_D1-2_D3-7可联承诺前后半小时未处理订单明细/ D0_D1-2_D3-7 Detail order yang bisa dihubungi sudah janji bayar belum diproses </h3>");
        // 表8 表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("阶段/ Tahapan")
                .append("<th>").append("日期/ Tanggal")
                .append("<th>").append("组长/ TL")
                .append("<th>").append("催收员/ Collector")
                .append("<th>").append("订单编号/ Order ID")
                .append("<th>").append("承诺还款时间/ Waktu janji bayar")
        ;
        List<D0ToD7CanCommitmentOrderDetails> D0ToD7CanCommitmentOrderDetailsList = collectionOrderHistoryDao
                .getD0ToD7CanCommitmentOrderDetails();
        for (D0ToD7CanCommitmentOrderDetails info : D0ToD7CanCommitmentOrderDetailsList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getStep())
                    .append("<td align=\"center\">").append(info.getCreateDay())
                    .append("<td align=\"center\">").append(info.getParentName())
                    .append("<td align=\"center\">").append(info.getStudentName())
                    .append("<td align=\"center\">").append(info.getOrderNo())
                    .append("<td align=\"center\">").append(info.getPromiseRepaymentTime())
            ;
        }
        sb.append("</table>");

        // 表9 标题：D0当日未还款订单WA和电话没有全部标亮订单明细
        sb.append("<h3>9.D0 未还款订单WA和电话没有全部标亮订单明细/ D0 Detail order yang WA dan telepon belum semua dihighlight </h3>");
        // 表9 表格体
        sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("<tr>")
                .append("<th>").append("阶段/ Tahapan")
                .append("<th>").append("日期/ Tanggal")
                .append("<th>").append("组长/ TL")
                .append("<th>").append("催收员/ Collector")
                .append("<th>").append("订单编号/ Order ID")
        ;
        List<D0NoLightUpOrderDetails> D0NoLightUpOrderDetailsList = collectionOrderHistoryDao
                .getD0NoLightUpOrderDetails();
        for (D0NoLightUpOrderDetails info : D0NoLightUpOrderDetailsList) {
            sb.append("<tr>")
                    .append("<td align=\"center\">").append(info.getStep())
                    .append("<td align=\"center\">").append(info.getCreateDay())
                    .append("<td align=\"center\">").append(info.getParentName())
                    .append("<td align=\"center\">").append(info.getStudentName())
                    .append("<td align=\"center\">").append(info.getOrderuuid())
            ;
        }
        sb.append("</table>");

        return sb.toString();
    }
}

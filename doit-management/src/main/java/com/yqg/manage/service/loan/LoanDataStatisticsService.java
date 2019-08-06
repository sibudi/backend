package com.yqg.manage.service.loan;


import com.yqg.common.constants.RedisContants;
import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.DateUtils;
import com.yqg.common.utils.JsonUtils;
import com.yqg.datasource.TargetDataSource;
import com.yqg.manage.service.loan.response.ManTodayDataStatisticsResponse;
import com.yqg.order.entity.ManLoanDataStatistics;
import com.yqg.order.entity.ManTodayDataStatistics;
import com.yqg.service.order.OrdService;
import com.yqg.order.entity.ManOrderSecondLoanSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;


/**
 * @author alan
 */
@Component
public class LoanDataStatisticsService {

    @Autowired
    private ManLoanDataStatisticsService manLoanDataStatisticsService;

    @Autowired
    private ManTodayDataStatisticsService manTodayDataStatisticsService;

    @Autowired
    private OrdService orderOrderService;

    @Autowired
    private ManOrderCountService manOrderCountService;

    @Autowired
    private RedisClient redisClient;

    /**
     * 数据app贷款总额数据统计*/
    public ManLoanDataStatistics getLoanDailyData() throws Exception {
        /*在贷余额*/
        BigDecimal loaningAmount = this.formatBigDecimal(this.orderOrderService.orderListSumByStatus("7,8"));
        /*在贷订单数*/
        Integer loaningCount = this.orderOrderService.orderListCountByStatus("7,8");

        BigDecimal dueAmount = this.manOrderCountService.orderSumByRefundTimeStatus(String.valueOf(OrdStateEnum.RESOLVING_NOT_OVERDUE.getCode()), DateUtils.dateToDay());
        /*到期未还总额*/
        dueAmount = this.formatBigDecimal(dueAmount);
        /*到期未还订单数*/
        Integer dueCount = this.orderOrderService.orderCountByRefundTimeStatus(String.valueOf(OrdStateEnum.RESOLVING_NOT_OVERDUE.getCode()), DateUtils.dateToDay());
        /*逾期未还总额*/
        BigDecimal overDueAmount = this.formatBigDecimal(this.manOrderCountService.orderSumByStatus("8"));
        /*逾期未还订单数*/
        Integer overDueCount = this.orderOrderService.orderListCountByStatus("8");

        /*未到还款日总额*/
        BigDecimal notPaidAmount = loaningAmount.subtract(dueAmount).subtract(overDueAmount).setScale(2,BigDecimal.ROUND_HALF_UP);
        /*未到还款日订单数*/
        Integer notPaidCount = loaningCount - dueCount -overDueCount;
        /*当日到期应还金额*/
        //BigDecimal todayShouldPayAmount = this.formatBigDecimal(this.manOrderCountService.orderSumByRefundTimeStatus("5,6",DateUtils.dateToDay()));
        /*到期应还金额*/
        BigDecimal expireShouldPayAmount = this.formatBigDecimal(this.manOrderCountService.sumOrderExpireShouldPayAmount(DateUtils.dateToDay()));//overDueAmount.add(todayShouldPayAmount);
        /*在贷逾期率*/
        BigDecimal loaningRate = overDueAmount.divide(loaningAmount,10,BigDecimal.ROUND_HALF_UP);
        /*应还逾期率*/
        BigDecimal shouldPaidRate = overDueAmount.divide(expireShouldPayAmount,10,BigDecimal.ROUND_HALF_UP);

        BigDecimal expectBadDebtRate = shouldPaidRate.multiply(new BigDecimal(0.35)).setScale(20,BigDecimal.ROUND_HALF_UP);   /*预计坏账率*/
        /*预计坏账金额*/
        BigDecimal expectBadDebtAmount = overDueAmount.multiply(new BigDecimal(0.35)).setScale(2,BigDecimal.ROUND_HALF_UP);

        ManLoanDataStatistics loanData = new ManLoanDataStatistics();
        loanData.setLoaningAmount(String.valueOf(loaningAmount.intValue()));    /*在贷余额*/
        loanData.setDueAmount(String.valueOf(dueAmount.intValue()));    /*到期未还总额*/
        loanData.setDueCount(dueCount.toString());      /*到期未还订单数*/
        loanData.setOverDueAmount(String.valueOf(overDueAmount.intValue()));    /*逾期未还总额*/
        loanData.setOverDueCount(overDueCount.toString());      /*逾期未还订单数*/
        loanData.setNotPaidAmount(String.valueOf(notPaidAmount.intValue()));    /*未到还款日总额*/
        loanData.setNotPaidCount(notPaidCount.toString());      /*未到还款日订单数*/
        loanData.setLoaningRate(loaningRate.toString());        /*在贷逾期率*/
        loanData.setExpireShouldPayAmount(String.valueOf(expireShouldPayAmount.intValue()));    /*到期应还金额*/
        loanData.setShouldPaidRate(shouldPaidRate.toString());  /*应还逾期率*/
        loanData.setExpectBadDebtRate(expectBadDebtRate.toString());    /*预计坏账率*/
        loanData.setExpectBadDebtAmount(String.valueOf(expectBadDebtAmount.intValue()));    /*预计坏账总额*/

        return loanData;
    }

    /**
     * 将在贷款总额数据统计保存到redis中*/
    @TargetDataSource(name="read")
    public void saveLoanData2Redis() throws Exception {
        ManLoanDataStatistics data = this.getLoanDailyData();
        this.redisClient.set(RedisContants.MANAGE_LOAN_DATA_STATISTICS, JsonUtils.serialize(data));
    }

    /**
     * 将在贷款总额数据统计保存到mysql中*/
    public void saveLoanData2Mysql() throws Exception {
        ManLoanDataStatistics data = this.getLoanDailyData();
        data.setCreateTime(new Date());
        this.manLoanDataStatisticsService.addManLoanDataStatistics(data);
    }

    /**
     * 从redis中取出贷款总额数据统计*/
    public ManLoanDataStatistics getLoanDataFromRedis() throws Exception {
        String data = this.redisClient.get(RedisContants.MANAGE_LOAN_DATA_STATISTICS);
        ManLoanDataStatistics response = JsonUtils.deserialize(data,ManLoanDataStatistics.class);
        return response;
    }

    /**
     * 统计今日订单数据*/
    public ManTodayDataStatistics todayDataStatistics() throws Exception {
        ManTodayDataStatistics data = new ManTodayDataStatistics();
        /*今日放款总额*/
        BigDecimal todayLoanAmount = this.formatBigDecimal(this.manOrderCountService.orderSumByLendingTimeStatus("7,10",DateUtils.dateToDay()));

        /*累积放款总额*/
        BigDecimal totalLoanAmount = this.formatBigDecimal(this.orderOrderService.orderListSumByStatus("7,8,10,11"));

        /*累计放款单数*/
        Integer totalLoanCount = this.orderOrderService.orderListCountByStatus("7,8,10,11");

        /*今日放款订单数*/
        Integer todayLoanCount = this.manOrderCountService.orderCountByLendingTimeStatus("7,10",DateUtils.dateToDay());

        /*今日放款用户数*/
        List<Integer> todayLoanUserCountResult = this.manOrderCountService.todayLoanUserCount(DateUtils.dateToDay());
        Integer todayLoanUserCount = todayLoanUserCountResult.size();

        /*累积放款用户数*/
        //List<Integer> totalLoanUserCountResult = this.manOrderCountService.totalLoanUserCount();
        Integer totalLoanUserCount = this.manOrderCountService.totalLoanUserCount();

        /*今日还款总额*/
        BigDecimal todayRepaymentAmount = this.formatBigDecimal(this.manOrderCountService.todayRepaymentAmount(DateUtils.dateToDay()));

        /*累积还款总额*/
        BigDecimal totalRepaymentAmount = this.formatBigDecimal(this.orderOrderService.getRepaymentTotalAmount());

        /*正常还款总额*/
        BigDecimal todayNormalRepaymentAmount = this.formatBigDecimal(this.manOrderCountService.todayNormalRepaymentAmount(DateUtils.dateToDay()));

        /*正常还款用户数*/
        Integer todayNormalRepaymentCountResult = this.manOrderCountService.todayNormalRepaymentUserCount();
        Integer todayNormalRepaymentUserCount = 0;
        Integer todayNormalRepaymentOrderCount = 0;     /*正常还款订单数*/
        if(todayNormalRepaymentCountResult == null){
        }else{
            todayNormalRepaymentUserCount = todayNormalRepaymentCountResult;
            todayNormalRepaymentOrderCount = todayNormalRepaymentCountResult;
        }

        /*逾期还款总额*/
        BigDecimal todayOverDueRepaymentAmount = this.formatBigDecimal(this.manOrderCountService.todayOverDueRepaymentAmount());

        Integer todayOverDueRepaymentCountResult = this.manOrderCountService.todayOverDueRepaymentUserCount();
        Integer todayOverDueRepaymentUserCount = 0;     /*逾期还款用户数*/
        Integer todayOverDueRepaymentOrderCount = 0;    /*逾期还款订单数*/
        if(todayOverDueRepaymentCountResult == null){
        }else{
            todayOverDueRepaymentUserCount = todayOverDueRepaymentCountResult;
            todayOverDueRepaymentOrderCount = todayOverDueRepaymentCountResult;
        }

        /*提前还款总额*/
        BigDecimal todayPrepaymentAmount = todayRepaymentAmount.subtract(todayNormalRepaymentAmount).subtract(todayOverDueRepaymentAmount).setScale(2,BigDecimal.ROUND_HALF_UP);

        /*提前还款用户数*/
        //Integer todayPrepaymentOrderCountResult = ;
        Integer todayPrepaymentUserCount = this.manOrderCountService.todayPrepaymentUserCount(DateUtils.dateToDay());
        Integer todayPrepaymentOrderCount = this.manOrderCountService.todayPrepaymentOrderSum(DateUtils.dateToDay());      /*提前还款订单数*/
        if(todayPrepaymentUserCount == null ){
            todayPrepaymentUserCount = 0;
        }
        if(todayPrepaymentOrderCount == null){
            todayPrepaymentOrderCount = 0;
        }

        /*今日逾期总额*/
        BigDecimal todayOverDueAmount = this.formatBigDecimal(this.manOrderCountService.todayOverDueAmountSum(DateUtils.dateToDay()));
        /*累积逾期总额*/
        BigDecimal totalOverDueAmount = this.formatBigDecimal(this.manOrderCountService.orderSumByStatus("8"));

        /*今日逾期用户数*/
        Integer todayOverDueCountResult = this.manOrderCountService.todayOverDueUserCountResult();
        Integer todayOverDueUserCount = 0;
        Integer todayOverDueOrderCount = 0;       /*今日逾期订单数*/
        if(todayOverDueCountResult == null){
        }else {
            todayOverDueUserCount = todayOverDueCountResult;
            todayOverDueOrderCount = todayOverDueCountResult;
        }

        /*累积逾期用户数*/
        Integer totalOverDueUserCount = this.manOrderCountService.orderCountByStatus(String.valueOf(OrdStateEnum.RESOLVING_OVERDUE.getCode()));
        /*累积逾期订单数*/
        Integer totalOverDueOrderCount = totalOverDueUserCount;

        /*今日复借总额*/
        BigDecimal todayRepeatOrderAmount = this.formatBigDecimal(this.manOrderCountService.todayRepeatOrderSum(DateUtils.dateToDay()));

        /*累积复借总额*/
        BigDecimal totalRepeatOrderAmount = this.formatBigDecimal(this.manOrderCountService.totalRepeatOrderSum());

        /*今日复借用户数*/
        Integer todayRepeatUserResult = this.manOrderCountService.todayRepeatUserCountResult();
        Integer todayRepeatUserCount = 0;
        Integer todayRepeatOrderCount = 0;      /*今日复借订单数*/
        if(todayRepeatUserResult == null){
        }else{
            todayRepeatUserCount = todayRepeatUserResult;
            todayRepeatOrderCount = todayRepeatUserResult;
        }

        /*累积复借用户数*/
        List<ManOrderSecondLoanSpec> totalRepeatUserResult = this.orderOrderService.secondLoanTotalCount();
        Integer totalRepeatOrderCount = 0;      /*累积复借订单数*/
        Integer totalRepeatUserCount = 0;      /*累积复借用户数*/
        totalRepeatUserCount = totalRepeatUserResult.size();
        for(ManOrderSecondLoanSpec item:totalRepeatUserResult){
            totalRepeatOrderCount = item.getUserCount() + totalRepeatOrderCount;
        }

        /*今日复借率 = 今日复借订单数/今日放款订单数 */
        BigDecimal todayRepeatOrderRate = new BigDecimal(0.00);
        if(todayLoanCount > 0){
            todayRepeatOrderRate = new BigDecimal(todayRepeatOrderCount).divide(new BigDecimal(todayLoanCount),10,BigDecimal.ROUND_HALF_UP);
        }

        /*累积复借率 = 累积复借订单数/累积放款订单数 */
        BigDecimal totalRepeatOrderRate = new BigDecimal(0.00);
        if(totalLoanCount > 0){
            totalRepeatOrderRate = new BigDecimal(totalRepeatOrderCount).divide(new BigDecimal(totalLoanCount),10,BigDecimal.ROUND_HALF_UP);
        }

        /*逾期3天内订单数*/
        Integer lessThreeDayOrder = this.manOrderCountService.orderCountByoverDueDay(3,1);
        /*逾期4-7天内订单数*/
        Integer betweenFourSevenOrder = this.manOrderCountService.orderCountByoverDueDay(7,4);
        /*逾期7天以上订单数*/
        Integer moreSevenOrder = this.manOrderCountService.orderCountByoverDueDay(500,8);

        data.setTodayLoanAmount(String.valueOf(todayLoanAmount.intValue()));        /*今日放款总额*/
        data.setTotalLoanAmount(String.valueOf(totalLoanAmount.intValue()));        /*累积放款总额*/
        data.setTotalLoanCount(totalLoanCount.toString());          /*累计放款单数*/
        data.setTodayLoanCount(todayLoanCount.toString());          /*今日放款订单数*/
        data.setTodayLoanUserCount(todayLoanUserCount.toString());  /*今日放款用户数*/
        data.setTotalLoanUserCount(totalLoanUserCount.toString());  /*累积放款用户数*/
        data.setTodayRepaymentAmount(todayRepaymentAmount.setScale(0, RoundingMode.HALF_UP).toString()); /*今日还款总额*/
        data.setTotalRepaymentAmount(totalRepaymentAmount.setScale(0, RoundingMode.HALF_UP).toString());  /*累积还款总额*/
        data.setTodayNormalRepaymentAmount(String.valueOf(todayNormalRepaymentAmount.intValue()));    /*正常还款总额*/
        data.setTodayNormalRepaymentUserCount(todayNormalRepaymentUserCount.toString());   /*正常还款用户数*/
        data.setTodayNormalRepaymentOrderCount(todayNormalRepaymentOrderCount.toString());  /*正常还款订单数*/
        data.setTodayOverDueRepaymentAmount(String.valueOf(todayOverDueRepaymentAmount.intValue()));        /*逾期还款总额*/
        data.setTodayOverDueRepaymentUserCount(todayOverDueRepaymentUserCount.toString());  /*逾期还款用户数*/
        data.setTodayOverDueRepaymentOrderCount(todayOverDueRepaymentOrderCount.toString());    /*逾期还款订单数*/
        data.setTodayPrepaymentAmount(String.valueOf(todayPrepaymentAmount.intValue()));    /*提前还款总额*/
        data.setTodayPrepaymentUserCount(todayPrepaymentUserCount.toString());      /*提前还款用户数*/
        data.setTodayPrepaymentOrderCount(todayPrepaymentOrderCount.toString());    /*提前还款订单数*/
        data.setTodayOverDueAmount(String.valueOf(todayOverDueAmount.intValue()));      /*今日逾期总额*/
        data.setTotalOverDueAmount(String.valueOf(totalOverDueAmount.intValue()));      /*累积逾期总额*/
        data.setTodayOverDueUserCount(todayOverDueUserCount.toString());    /*今日逾期用户数*/
        data.setTodayOverDueOrderCount(todayOverDueOrderCount.toString());  /*今日逾期订单数*/
        data.setTotalOverDueUserCount(totalOverDueUserCount.toString());    /*累积逾期用户数*/
        data.setTotalOverDueOrderCount(totalOverDueOrderCount.toString());  /*累积逾期订单数*/
        data.setTodayRepeatOrderAmount(String.valueOf(todayRepeatOrderAmount.intValue()));  /*今日复借总额*/
        data.setTotalRepeatOrderAmount(String.valueOf(totalRepeatOrderAmount.intValue()));  /*累积复借总额*/
        data.setTodayRepeatUserCount(todayRepeatUserCount.toString());      /*今日复借用户数*/
        data.setTodayRepeatOrderCount(todayRepeatOrderCount.toString());    /*今日复借订单数*/
        data.setTotalRepeatOrderCount(totalRepeatOrderCount.toString());    /*累积复借订单数*/
        data.setTotalRepeatUserCount(totalRepeatUserCount.toString());      /*累积复借用户数*/
        data.setTodayRepeatOrderRate(todayRepeatOrderRate.toString());      /*今日复借率*/
        data.setTotalRepeatOrderRate(totalRepeatOrderRate.toString());      /*累积复借率*/
        data.setLessThreeDayOrder(lessThreeDayOrder.toString());            /*逾期3天内订单数*/
        data.setBetweenFourSevenOrder(betweenFourSevenOrder.toString());    /*逾期4-7天内订单数*/
        data.setMoreSevenOrder(moreSevenOrder.toString());                  /*逾期7天以上订单数*/

        return data;
    }

    public static void main(String[] args) {
        BigDecimal b = new BigDecimal("481231300.00");

        System.out.print(b.setScale(0,RoundingMode.HALF_UP).toString());
    }

    public ManTodayDataStatisticsResponse caclCycleRateData(ManTodayDataStatistics result) throws Exception {
        ManTodayDataStatisticsResponse data = new ManTodayDataStatisticsResponse();

        data.setTodayLoanAmount(result.getTodayLoanAmount());        /*今日放款总额*/
        data.setTotalLoanAmount(result.getTotalLoanAmount());        /*累积放款总额*/
        data.setTotalLoanCount(result.getTotalLoanCount());          /*累计放款单数*/
        data.setTodayLoanCount(result.getTodayLoanCount());          /*今日放款订单数*/
        data.setTodayLoanUserCount(result.getTodayLoanUserCount());  /*今日放款用户数*/
        data.setTotalLoanUserCount(result.getTotalLoanUserCount());  /*累积放款用户数*/
        data.setTodayRepaymentAmount(result.getTodayRepaymentAmount()); /*今日还款总额*/
        data.setTotalRepaymentAmount(result.getTotalRepaymentAmount());  /*累积还款总额*/
        data.setTodayNormalRepaymentAmount(result.getTodayNormalRepaymentAmount());    /*正常还款总额*/
        data.setTodayNormalRepaymentUserCount(result.getTodayNormalRepaymentUserCount());   /*正常还款用户数*/
        data.setTodayNormalRepaymentOrderCount(result.getTodayNormalRepaymentOrderCount());  /*正常还款订单数*/
        data.setTodayOverDueRepaymentAmount(result.getTodayOverDueRepaymentAmount());        /*逾期还款总额*/
        data.setTodayOverDueRepaymentUserCount(result.getTodayOverDueRepaymentUserCount());  /*逾期还款用户数*/
        data.setTodayOverDueRepaymentOrderCount(result.getTodayOverDueRepaymentOrderCount());    /*逾期还款订单数*/
        data.setTodayPrepaymentAmount(result.getTodayPrepaymentAmount());    /*提前还款总额*/
        data.setTodayPrepaymentUserCount(result.getTodayPrepaymentUserCount());      /*提前还款用户数*/
        data.setTodayPrepaymentOrderCount(result.getTodayPrepaymentOrderCount());    /*提前还款订单数*/
        data.setTodayOverDueAmount(result.getTodayOverDueAmount());      /*今日逾期总额*/
        data.setTotalOverDueAmount(result.getTotalOverDueAmount());      /*累积逾期总额*/
        data.setTodayOverDueUserCount(result.getTodayOverDueUserCount());    /*今日逾期用户数*/
        data.setTodayOverDueOrderCount(result.getTodayOverDueOrderCount());  /*今日逾期订单数*/
        data.setTotalOverDueUserCount(result.getTotalOverDueUserCount());    /*累积逾期用户数*/
        data.setTotalOverDueOrderCount(result.getTotalOverDueOrderCount());  /*累积逾期订单数*/
        data.setTodayRepeatOrderAmount(result.getTodayRepeatOrderAmount());  /*今日复借总额*/
        data.setTotalRepeatOrderAmount(result.getTotalRepeatOrderAmount());  /*累积复借总额*/
        data.setTodayRepeatUserCount(result.getTodayRepeatUserCount());      /*今日复借用户数*/
        data.setTodayRepeatOrderCount(result.getTodayRepeatOrderCount());    /*今日复借订单数*/
        data.setTotalRepeatOrderCount(result.getTotalRepeatOrderCount());    /*累积复借订单数*/
        data.setTotalRepeatUserCount(result.getTotalRepeatUserCount());      /*累积复借用户数*/
        data.setTodayRepeatOrderRate(result.getTodayRepeatOrderRate());      /*今日复借率*/
        data.setTotalRepeatOrderRate(result.getTotalRepeatOrderRate());      /*累积复借率*/
        data.setLessThreeDayOrder(result.getLessThreeDayOrder());            /*逾期3天内订单数*/
        data.setBetweenFourSevenOrder(result.getBetweenFourSevenOrder());    /*逾期4-7天内订单数*/
        data.setMoreSevenOrder(result.getMoreSevenOrder());                  /*逾期7天以上订单数*/

        String yesterdayResult = this.redisClient.get(RedisContants.MANAGE_YESTERDAY_DATA_STATISTICS);
        if(yesterdayResult == null){
            data = this.setDefaultCycleRate(data);
        }else{
            ManTodayDataStatisticsResponse yesterdayData = JsonUtils.deserialize(yesterdayResult,ManTodayDataStatisticsResponse.class);

            /*今日放款总额环比*/
            data.setTodayLoanCycleRate(this.formatCycleRate(result.getTodayLoanAmount(),yesterdayData.getTodayLoanAmount()));
            /*今日放款单数环比*/
            data.setTodayLoanCountCycleRate(this.formatCycleRate(result.getTodayLoanCount(),yesterdayData.getTodayLoanCount()));
            /*今日放款用户数环比*/
            data.setTodayLoanUserCountCycleRate(this.formatCycleRate(result.getTodayLoanUserCount(),yesterdayData.getTodayLoanUserCount()));
            /*今日还款总额环比*/
            data.setTodayRepaymentAmountCycleRate(this.formatCycleRate(result.getTodayRepaymentAmount(),yesterdayData.getTodayRepaymentAmount()));
            /*正常还款总额环比*/
            data.setTodayNormalRepaymentAmountCycleRate(this.formatCycleRate(result.getTodayNormalRepaymentAmount(),yesterdayData.getTodayNormalRepaymentAmount()));
            /*正常还款单数环比*/
            data.setTodayNormalRepaymentOrderCountCycleRate(this.formatCycleRate(result.getTodayNormalRepaymentOrderCount(),yesterdayData.getTodayNormalRepaymentOrderCount()));
            /*正常还款用户数环比*/
            data.setTodayNormalRepaymentUserCountCycleRate(this.formatCycleRate(result.getTodayNormalRepaymentUserCount(),yesterdayData.getTodayNormalRepaymentUserCount()));
            /*逾期还款总额环比*/
            data.setTodayOverDueRepaymentAmountCycleRate(this.formatCycleRate(result.getTodayOverDueRepaymentAmount(),yesterdayData.getTodayOverDueRepaymentAmount()));
            /*逾期还款单数环比*/
            data.setTodayOverDueRepaymentOrderCountCycleRate(this.formatCycleRate(result.getTodayOverDueRepaymentOrderCount(),yesterdayData.getTodayOverDueRepaymentOrderCount()));
            /*逾期还款用户数环比*/
            data.setTodayOverDueRepaymentUserCountCycleRate(this.formatCycleRate(result.getTodayOverDueRepaymentUserCount(),yesterdayData.getTodayOverDueRepaymentUserCount()));
            /*提前还款总额环比*/
            data.setTodayPrepaymentAmountCycleRate(this.formatCycleRate(result.getTodayPrepaymentAmount(),yesterdayData.getTodayPrepaymentAmount()));
            /*提前还款用户数环比*/
            data.setTodayPrepaymentUserCounCycleRate(this.formatCycleRate(result.getTodayPrepaymentUserCount(),yesterdayData.getTodayPrepaymentUserCount()));
            /*提前还款订单数环比*/
            data.setTodayPrepaymentOrderCountCycleRate(this.formatCycleRate(result.getTodayPrepaymentOrderCount(),yesterdayData.getTodayPrepaymentOrderCount()));
            /*今日逾期总额环比*/
            data.setTodayOverDueAmountCycleRate(this.formatCycleRate(result.getTodayOverDueAmount(),yesterdayData.getTodayOverDueAmount()));
            /*今日逾期用户数环比*/
            data.setTodayOverDueUserCountCycleRate(this.formatCycleRate(result.getTodayOverDueUserCount(),yesterdayData.getTodayOverDueUserCount()));
            /*今日逾期订单数环比*/
            data.setTodayOverDueOrderCountCycleRate(this.formatCycleRate(result.getTodayOverDueOrderCount(),yesterdayData.getTodayOverDueOrderCount()));
            /*今日复借总额环比*/
            data.setTodayRepeatOrderAmountCycleRate(this.formatCycleRate(result.getTodayRepeatOrderAmount(),yesterdayData.getTodayRepeatOrderAmount()));
            /*今日复借用户数环比*/
            data.setTodayRepeatUserCountCycleRate(this.formatCycleRate(result.getTodayRepeatUserCount(),yesterdayData.getTodayRepeatUserCount()));
            /*今日复借单数环比*/
            data.setTodayRepeatOrderCountCycleRate(this.formatCycleRate(result.getTodayRepeatOrderCount(),yesterdayData.getTodayRepeatOrderCount()));
            /*今日复借率环比*/
            data.setTodayRepeatOrderRateCycleRate(this.formatCycleRate(result.getTodayRepeatOrderRate(),yesterdayData.getTodayRepeatOrderRate()));
        }
        return data;
    }

    /**
     * 若昨日无数据将环比数据设为默认值*/
    public ManTodayDataStatisticsResponse setDefaultCycleRate(ManTodayDataStatisticsResponse data) throws Exception {
        String defaultValue = "0.00000000";
        data.setTodayLoanCycleRate(defaultValue);
        data.setTodayLoanCountCycleRate(defaultValue);
        data.setTodayLoanUserCountCycleRate(defaultValue);
        data.setTodayRepaymentAmountCycleRate(defaultValue);
        data.setTodayNormalRepaymentAmountCycleRate(defaultValue);
        data.setTodayNormalRepaymentOrderCountCycleRate(defaultValue);
        data.setTodayNormalRepaymentUserCountCycleRate(defaultValue);
        data.setTodayOverDueRepaymentAmountCycleRate(defaultValue);
        data.setTodayOverDueRepaymentOrderCountCycleRate(defaultValue);
        data.setTodayOverDueRepaymentUserCountCycleRate(defaultValue);
        data.setTodayPrepaymentAmountCycleRate(defaultValue);
        data.setTodayPrepaymentUserCounCycleRate(defaultValue);
        data.setTodayPrepaymentOrderCountCycleRate(defaultValue);
        data.setTodayOverDueAmountCycleRate(defaultValue);
        data.setTodayOverDueUserCountCycleRate(defaultValue);
        data.setTodayOverDueOrderCountCycleRate(defaultValue);
        data.setTodayRepeatOrderAmountCycleRate(defaultValue);
        data.setTodayRepeatUserCountCycleRate(defaultValue);
        data.setTodayRepeatOrderCountCycleRate(defaultValue);
        data.setTodayRepeatOrderRateCycleRate(defaultValue);
        return data;
    }

    /**
     * 将今日数据缓存入redis*/
    @TargetDataSource(name="read")
    public void putTodayData2Redis() throws Exception {
        ManTodayDataStatistics result = this.todayDataStatistics();
        ManTodayDataStatisticsResponse response = this.caclCycleRateData(result);
        this.redisClient.set(RedisContants.MANAGE_TODAY_DATA_STATISTICS,JsonUtils.serialize(response));
    }

    /**
     * 统计今日数据存入数据库中*/
    public void putTodayData2Mysql() throws Exception {
        ManTodayDataStatistics result = this.todayDataStatistics();
        this.redisClient.set(RedisContants.MANAGE_YESTERDAY_DATA_STATISTICS,JsonUtils.serialize(result));
        this.manTodayDataStatisticsService.addManTodayDataStatistics(result);
    }

    /**
     * 在redis中取今日数据缓存*/
    public ManTodayDataStatisticsResponse getTodayDataFromRedis() throws Exception {
        String result = this.redisClient.get(RedisContants.MANAGE_TODAY_DATA_STATISTICS);
        ManTodayDataStatisticsResponse response = JsonUtils.deserialize(result,ManTodayDataStatisticsResponse.class);
        return response;
    }

    public BigDecimal formatBigDecimal(BigDecimal data) {
        if(data == null){
            data = new BigDecimal(0.00);
        }
        data = data.divide(new BigDecimal(10000),2,BigDecimal.ROUND_HALF_UP);
        return data;
    }

    public String formatCycleRate(String today,String yesterday) throws Exception {
        BigDecimal todayData = new BigDecimal(today);
        BigDecimal yesterdayData = new BigDecimal(yesterday);
        if(yesterday == null || yesterdayData.compareTo(new BigDecimal(0.00)) == 0){
            return "0.00000000";
        }
        BigDecimal result = (todayData.subtract(yesterdayData)).divide(yesterdayData,10,BigDecimal.ROUND_HALF_UP);
        if(result.compareTo(new BigDecimal(0.00)) == 0){
            return "0.00000000";
        }
        return result.toString();
    }
}

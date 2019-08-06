package com.yqg.manage.service.loan.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;

/**
 * @author alan
 */
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
public class ManTodayDataStatisticsResponse {
    private String todayLoanAmount;

    private String totalLoanAmount;

    private String totalLoanCount;

    private String todayLoanCount;

    private String todayLoanUserCount;

    private String totalLoanUserCount;

    private String todayRepaymentAmount;

    private String totalRepaymentAmount;

    private String todayNormalRepaymentAmount;

    private String todayNormalRepaymentUserCount;

    private String todayNormalRepaymentOrderCount;

    private String todayOverDueRepaymentAmount;

    private String todayOverDueRepaymentUserCount;

    private String todayOverDueRepaymentOrderCount;

    private String todayPrepaymentAmount;

    private String todayPrepaymentUserCount;

    private String todayPrepaymentOrderCount;

    private String todayOverDueAmount;

    private String totalOverDueAmount;

    private String todayOverDueUserCount;

    private String todayOverDueOrderCount;

    private String totalOverDueUserCount;

    private String totalOverDueOrderCount;

    private String todayRepeatOrderAmount;

    private String totalRepeatOrderAmount;

    private String todayRepeatUserCount;

    private String todayRepeatOrderCount;

    private String totalRepeatOrderCount;

    private String totalRepeatUserCount;

    private String todayRepeatOrderRate;

    private String totalRepeatOrderRate;

    private String lessThreeDayOrder;

    private String betweenFourSevenOrder;

    private String moreSevenOrder;

    private String todayLoanCycleRate;  /*今日放款总额环比*/

    private String todayLoanCountCycleRate;     /*今日放款单数环比*/

    private String todayLoanUserCountCycleRate;       /*今日放款用户数环比*/

    private String todayRepaymentAmountCycleRate;       /*今日还款总额环比*/

    private String todayNormalRepaymentAmountCycleRate;        /*正常还款总额环比*/

    private String todayNormalRepaymentOrderCountCycleRate;        /*正常还款单数环比*/

    private String todayNormalRepaymentUserCountCycleRate;       /*正常还款用户数环比*/

    private String todayOverDueRepaymentAmountCycleRate;        /*逾期还款总额环比*/

    private String todayOverDueRepaymentOrderCountCycleRate;        /*逾期还款总额单数环比*/

    private String todayOverDueRepaymentUserCountCycleRate;        /*逾期还款总用户数环比*/

    private String todayPrepaymentAmountCycleRate;        /*提前还款总额环比*/

    private String todayPrepaymentUserCounCycleRate;        /*提前还款用户数环比*/

    private String todayPrepaymentOrderCountCycleRate;        /*提前还款订单数环比*/

    private String todayOverDueAmountCycleRate;        /*今日逾期总额环比*/

    private String todayOverDueUserCountCycleRate;        /*今日逾期用户数环比*/

    private String todayOverDueOrderCountCycleRate;        /*今日逾期订单数环比*/

    private String todayRepeatOrderAmountCycleRate;        /*今日复借总额环比*/

    private String todayRepeatUserCountCycleRate;        /*今日复借用户数环比*/

    private String todayRepeatOrderCountCycleRate;        /*今日复借单数环比*/

    private String todayRepeatOrderRateCycleRate;        /*今日复借率环比*/

    public String getTodayLoanAmount() {
        return todayLoanAmount;
    }

    public String getTotalLoanAmount() {
        return totalLoanAmount;
    }

    public String getTotalLoanCount() {
        return totalLoanCount;
    }

    public String getTodayLoanCount() {
        return todayLoanCount;
    }

    public String getTodayLoanUserCount() {
        return todayLoanUserCount;
    }

    public String getTotalLoanUserCount() {
        return totalLoanUserCount;
    }

    public String getTodayRepaymentAmount() {
        return todayRepaymentAmount;
    }

    public String getTotalRepaymentAmount() {
        return totalRepaymentAmount;
    }

    public String getTodayNormalRepaymentAmount() {
        return todayNormalRepaymentAmount;
    }

    public String getTodayNormalRepaymentUserCount() {
        return todayNormalRepaymentUserCount;
    }

    public String getTodayNormalRepaymentOrderCount() {
        return todayNormalRepaymentOrderCount;
    }

    public String getTodayOverDueRepaymentAmount() {
        return todayOverDueRepaymentAmount;
    }

    public String getTodayOverDueRepaymentUserCount() {
        return todayOverDueRepaymentUserCount;
    }

    public String getTodayOverDueRepaymentOrderCount() {
        return todayOverDueRepaymentOrderCount;
    }

    public String getTodayPrepaymentAmount() {
        return todayPrepaymentAmount;
    }

    public String getTodayPrepaymentUserCount() {
        return todayPrepaymentUserCount;
    }

    public String getTodayPrepaymentOrderCount() {
        return todayPrepaymentOrderCount;
    }

    public String getTodayOverDueAmount() {
        return todayOverDueAmount;
    }

    public String getTotalOverDueAmount() {
        return totalOverDueAmount;
    }

    public String getTodayOverDueUserCount() {
        return todayOverDueUserCount;
    }

    public String getTodayOverDueOrderCount() {
        return todayOverDueOrderCount;
    }

    public String getTotalOverDueUserCount() {
        return totalOverDueUserCount;
    }

    public String getTotalOverDueOrderCount() {
        return totalOverDueOrderCount;
    }

    public String getTodayRepeatOrderAmount() {
        return todayRepeatOrderAmount;
    }

    public String getTotalRepeatOrderAmount() {
        return totalRepeatOrderAmount;
    }

    public String getTodayRepeatUserCount() {
        return todayRepeatUserCount;
    }

    public String getTodayRepeatOrderCount() {
        return todayRepeatOrderCount;
    }

    public String getTotalRepeatOrderCount() {
        return totalRepeatOrderCount;
    }

    public String getTotalRepeatUserCount() {
        return totalRepeatUserCount;
    }

    public String getTodayRepeatOrderRate() {
        return todayRepeatOrderRate;
    }

    public String getTotalRepeatOrderRate() {
        return totalRepeatOrderRate;
    }

    public String getLessThreeDayOrder() {
        return lessThreeDayOrder;
    }

    public String getBetweenFourSevenOrder() {
        return betweenFourSevenOrder;
    }

    public String getMoreSevenOrder() {
        return moreSevenOrder;
    }

    public String getTodayLoanCycleRate() {
        return todayLoanCycleRate;
    }

    public String getTodayLoanCountCycleRate() {
        return todayLoanCountCycleRate;
    }

    public String getTodayLoanUserCountCycleRate() {
        return todayLoanUserCountCycleRate;
    }

    public String getTodayRepaymentAmountCycleRate() {
        return todayRepaymentAmountCycleRate;
    }

    public String getTodayNormalRepaymentAmountCycleRate() {
        return todayNormalRepaymentAmountCycleRate;
    }

    public String getTodayNormalRepaymentOrderCountCycleRate() {
        return todayNormalRepaymentOrderCountCycleRate;
    }

    public String getTodayNormalRepaymentUserCountCycleRate() {
        return todayNormalRepaymentUserCountCycleRate;
    }

    public String getTodayOverDueRepaymentAmountCycleRate() {
        return todayOverDueRepaymentAmountCycleRate;
    }

    public String getTodayOverDueRepaymentOrderCountCycleRate() {
        return todayOverDueRepaymentOrderCountCycleRate;
    }

    public String getTodayOverDueRepaymentUserCountCycleRate() {
        return todayOverDueRepaymentUserCountCycleRate;
    }

    public String getTodayPrepaymentAmountCycleRate() {
        return todayPrepaymentAmountCycleRate;
    }

    public String getTodayPrepaymentUserCounCycleRate() {
        return todayPrepaymentUserCounCycleRate;
    }

    public String getTodayPrepaymentOrderCountCycleRate() {
        return todayPrepaymentOrderCountCycleRate;
    }

    public String getTodayOverDueAmountCycleRate() {
        return todayOverDueAmountCycleRate;
    }

    public String getTodayOverDueUserCountCycleRate() {
        return todayOverDueUserCountCycleRate;
    }

    public String getTodayOverDueOrderCountCycleRate() {
        return todayOverDueOrderCountCycleRate;
    }

    public String getTodayRepeatOrderAmountCycleRate() {
        return todayRepeatOrderAmountCycleRate;
    }

    public String getTodayRepeatUserCountCycleRate() {
        return todayRepeatUserCountCycleRate;
    }

    public String getTodayRepeatOrderCountCycleRate() {
        return todayRepeatOrderCountCycleRate;
    }

    public String getTodayRepeatOrderRateCycleRate() {
        return todayRepeatOrderRateCycleRate;
    }

    public void setTodayLoanAmount(String todayLoanAmount) {
        this.todayLoanAmount = todayLoanAmount;
    }

    public void setTotalLoanAmount(String totalLoanAmount) {
        this.totalLoanAmount = totalLoanAmount;
    }

    public void setTotalLoanCount(String totalLoanCount) {
        this.totalLoanCount = totalLoanCount;
    }

    public void setTodayLoanCount(String todayLoanCount) {
        this.todayLoanCount = todayLoanCount;
    }

    public void setTodayLoanUserCount(String todayLoanUserCount) {
        this.todayLoanUserCount = todayLoanUserCount;
    }

    public void setTotalLoanUserCount(String totalLoanUserCount) {
        this.totalLoanUserCount = totalLoanUserCount;
    }

    public void setTodayRepaymentAmount(String todayRepaymentAmount) {
        this.todayRepaymentAmount = todayRepaymentAmount;
    }

    public void setTotalRepaymentAmount(String totalRepaymentAmount) {
        this.totalRepaymentAmount = totalRepaymentAmount;
    }

    public void setTodayNormalRepaymentAmount(String todayNormalRepaymentAmount) {
        this.todayNormalRepaymentAmount = todayNormalRepaymentAmount;
    }

    public void setTodayNormalRepaymentUserCount(String todayNormalRepaymentUserCount) {
        this.todayNormalRepaymentUserCount = todayNormalRepaymentUserCount;
    }

    public void setTodayNormalRepaymentOrderCount(String todayNormalRepaymentOrderCount) {
        this.todayNormalRepaymentOrderCount = todayNormalRepaymentOrderCount;
    }

    public void setTodayOverDueRepaymentAmount(String todayOverDueRepaymentAmount) {
        this.todayOverDueRepaymentAmount = todayOverDueRepaymentAmount;
    }

    public void setTodayOverDueRepaymentUserCount(String todayOverDueRepaymentUserCount) {
        this.todayOverDueRepaymentUserCount = todayOverDueRepaymentUserCount;
    }

    public void setTodayOverDueRepaymentOrderCount(String todayOverDueRepaymentOrderCount) {
        this.todayOverDueRepaymentOrderCount = todayOverDueRepaymentOrderCount;
    }

    public void setTodayPrepaymentAmount(String todayPrepaymentAmount) {
        this.todayPrepaymentAmount = todayPrepaymentAmount;
    }

    public void setTodayPrepaymentUserCount(String todayPrepaymentUserCount) {
        this.todayPrepaymentUserCount = todayPrepaymentUserCount;
    }

    public void setTodayPrepaymentOrderCount(String todayPrepaymentOrderCount) {
        this.todayPrepaymentOrderCount = todayPrepaymentOrderCount;
    }

    public void setTodayOverDueAmount(String todayOverDueAmount) {
        this.todayOverDueAmount = todayOverDueAmount;
    }

    public void setTotalOverDueAmount(String totalOverDueAmount) {
        this.totalOverDueAmount = totalOverDueAmount;
    }

    public void setTodayOverDueUserCount(String todayOverDueUserCount) {
        this.todayOverDueUserCount = todayOverDueUserCount;
    }

    public void setTodayOverDueOrderCount(String todayOverDueOrderCount) {
        this.todayOverDueOrderCount = todayOverDueOrderCount;
    }

    public void setTotalOverDueUserCount(String totalOverDueUserCount) {
        this.totalOverDueUserCount = totalOverDueUserCount;
    }

    public void setTotalOverDueOrderCount(String totalOverDueOrderCount) {
        this.totalOverDueOrderCount = totalOverDueOrderCount;
    }

    public void setTodayRepeatOrderAmount(String todayRepeatOrderAmount) {
        this.todayRepeatOrderAmount = todayRepeatOrderAmount;
    }

    public void setTotalRepeatOrderAmount(String totalRepeatOrderAmount) {
        this.totalRepeatOrderAmount = totalRepeatOrderAmount;
    }

    public void setTodayRepeatUserCount(String todayRepeatUserCount) {
        this.todayRepeatUserCount = todayRepeatUserCount;
    }

    public void setTodayRepeatOrderCount(String todayRepeatOrderCount) {
        this.todayRepeatOrderCount = todayRepeatOrderCount;
    }

    public void setTotalRepeatOrderCount(String totalRepeatOrderCount) {
        this.totalRepeatOrderCount = totalRepeatOrderCount;
    }

    public void setTotalRepeatUserCount(String totalRepeatUserCount) {
        this.totalRepeatUserCount = totalRepeatUserCount;
    }

    public void setTodayRepeatOrderRate(String todayRepeatOrderRate) {
        this.todayRepeatOrderRate = todayRepeatOrderRate;
    }

    public void setTotalRepeatOrderRate(String totalRepeatOrderRate) {
        this.totalRepeatOrderRate = totalRepeatOrderRate;
    }

    public void setLessThreeDayOrder(String lessThreeDayOrder) {
        this.lessThreeDayOrder = lessThreeDayOrder;
    }

    public void setBetweenFourSevenOrder(String betweenFourSevenOrder) {
        this.betweenFourSevenOrder = betweenFourSevenOrder;
    }

    public void setMoreSevenOrder(String moreSevenOrder) {
        this.moreSevenOrder = moreSevenOrder;
    }

    public void setTodayLoanCycleRate(String todayLoanCycleRate) {
        this.todayLoanCycleRate = todayLoanCycleRate;
    }

    public void setTodayLoanCountCycleRate(String todayLoanCountCycleRate) {
        this.todayLoanCountCycleRate = todayLoanCountCycleRate;
    }

    public void setTodayLoanUserCountCycleRate(String todayLoanUserCountCycleRate) {
        this.todayLoanUserCountCycleRate = todayLoanUserCountCycleRate;
    }

    public void setTodayRepaymentAmountCycleRate(String todayRepaymentAmountCycleRate) {
        this.todayRepaymentAmountCycleRate = todayRepaymentAmountCycleRate;
    }

    public void setTodayNormalRepaymentAmountCycleRate(String todayNormalRepaymentAmountCycleRate) {
        this.todayNormalRepaymentAmountCycleRate = todayNormalRepaymentAmountCycleRate;
    }

    public void setTodayNormalRepaymentOrderCountCycleRate(String todayNormalRepaymentOrderCountCycleRate) {
        this.todayNormalRepaymentOrderCountCycleRate = todayNormalRepaymentOrderCountCycleRate;
    }

    public void setTodayNormalRepaymentUserCountCycleRate(String todayNormalRepaymentUserCountCycleRate) {
        this.todayNormalRepaymentUserCountCycleRate = todayNormalRepaymentUserCountCycleRate;
    }

    public void setTodayOverDueRepaymentAmountCycleRate(String todayOverDueRepaymentAmountCycleRate) {
        this.todayOverDueRepaymentAmountCycleRate = todayOverDueRepaymentAmountCycleRate;
    }

    public void setTodayOverDueRepaymentOrderCountCycleRate(String todayOverDueRepaymentOrderCountCycleRate) {
        this.todayOverDueRepaymentOrderCountCycleRate = todayOverDueRepaymentOrderCountCycleRate;
    }

    public void setTodayOverDueRepaymentUserCountCycleRate(String todayOverDueRepaymentUserCountCycleRate) {
        this.todayOverDueRepaymentUserCountCycleRate = todayOverDueRepaymentUserCountCycleRate;
    }

    public void setTodayPrepaymentAmountCycleRate(String todayPrepaymentAmountCycleRate) {
        this.todayPrepaymentAmountCycleRate = todayPrepaymentAmountCycleRate;
    }

    public void setTodayPrepaymentUserCounCycleRate(String todayPrepaymentUserCounCycleRate) {
        this.todayPrepaymentUserCounCycleRate = todayPrepaymentUserCounCycleRate;
    }

    public void setTodayPrepaymentOrderCountCycleRate(String todayPrepaymentOrderCountCycleRate) {
        this.todayPrepaymentOrderCountCycleRate = todayPrepaymentOrderCountCycleRate;
    }

    public void setTodayOverDueAmountCycleRate(String todayOverDueAmountCycleRate) {
        this.todayOverDueAmountCycleRate = todayOverDueAmountCycleRate;
    }

    public void setTodayOverDueUserCountCycleRate(String todayOverDueUserCountCycleRate) {
        this.todayOverDueUserCountCycleRate = todayOverDueUserCountCycleRate;
    }

    public void setTodayOverDueOrderCountCycleRate(String todayOverDueOrderCountCycleRate) {
        this.todayOverDueOrderCountCycleRate = todayOverDueOrderCountCycleRate;
    }

    public void setTodayRepeatOrderAmountCycleRate(String todayRepeatOrderAmountCycleRate) {
        this.todayRepeatOrderAmountCycleRate = todayRepeatOrderAmountCycleRate;
    }

    public void setTodayRepeatUserCountCycleRate(String todayRepeatUserCountCycleRate) {
        this.todayRepeatUserCountCycleRate = todayRepeatUserCountCycleRate;
    }

    public void setTodayRepeatOrderCountCycleRate(String todayRepeatOrderCountCycleRate) {
        this.todayRepeatOrderCountCycleRate = todayRepeatOrderCountCycleRate;
    }

    public void setTodayRepeatOrderRateCycleRate(String todayRepeatOrderRateCycleRate) {
        this.todayRepeatOrderRateCycleRate = todayRepeatOrderRateCycleRate;
    }
}

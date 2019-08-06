package com.yqg.order.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author alan
 */
@Table("manTodayDataStatistics")
public class ManTodayDataStatistics extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -4010800753006841934L;

    private Integer id;

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

    private Date createTime;

    public Integer getId() {
        return id;
    }

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

    public Date getCreateTime() {
        return createTime;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}

package com.yqg.drools.model;

import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstalledAppInfo {

    private Long appForLoanCount; //贷款app个数

    private BigDecimal appForLoanRatio;//贷款app占比

    private Long incrementalAppForLoanCount;

    private BigDecimal appForLoanRatioChange;

    private boolean hasLatestOrder;//是否有上笔订单

    private Long totalApps;//累计app个数

    private Long diffDaysBetweenLatestUpdateTimeAndCommitTime;//APP最后一次更新时间距提交的天数

    private Long diffDaysBetweenEarliestUpdateTimeAndCommitTime;//APP最早一次更新时间距提交的天数

    private Long diffDaysBetweenForEarliestAndLatestUpdateTime; //APP最后一次更新时间-最早一次更新时间 （单位：天）

    private Long appCountForNews;
    private Long appCountForEnterprise;
    private Long appCountForBeauty;
    private Long appCountForGambling;
    private Long appCountForCreditCard;
    private Long appCountForBeautyPicture;
    private Long appCountForPhotography;
    private Long appCountForEcommerce;
    private Long appCountForGame;
    private Long appCountForSocial;
    private Long appCountForTaxBPJS;
    private Long appCountForBank;
    private Long appCountForCinema;
    private Long appCountForTicket;


}

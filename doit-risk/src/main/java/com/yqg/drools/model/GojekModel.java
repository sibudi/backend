package com.yqg.drools.model;

import com.yqg.drools.beans.RideData.PersonBasicInfo;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

/*****
 * @Author zengxiangcai
 * created at ${date}
 * @email zengxiangcai@yishufu.com
 ****/

@Getter
@Setter
public class GojekModel {

    private BigDecimal totalDistanceFor180;
    private BigDecimal totalFareFor180;
    private Long totalCountFor180;
    private Long totalPickUpAddressCountFor180;
    private Long totalTaxiTypeCountFor180;
    private Long totalSpecialTaxiTypeCountFor180;

    private BigDecimal totalDistanceFor30;
    private BigDecimal totalFareFor30;
    private Long totalCountFor30;
    private Long totalPickUpAddressCountFor30;
    private Long totalTaxiTypeCountFor30;
    private Long totalSpecialTaxiTypeCountFor30;

    private Long diffDaysForFirstRideAndApplyTime;
    private Long diffDaysForLastRideAndApplyTime;

    private BigDecimal averageFarePerMonth;
    private BigDecimal averageRideCountPerMonth;

    private BigDecimal averageFare;
    private BigDecimal averageDistance;
    private BigDecimal maxFare;
    private BigDecimal maxDistance;

    private Long paymentMethodCount;
    private Long cashPayCount;

//    private BigDecimal coherenceForPickUpAndHomeAddress;// 不要
//    private BigDecimal coherenceForPickUpAndCompanyAddress;// 不要
//    private BigDecimal coherenceForPickUpAndSchoolAddress;// 不要
//
//    private BigDecimal coherenceForDropOffAndHomeAddress;// 不要
//    private BigDecimal coherenceForDropOffAndCompanyAddress;// 不要
//    private BigDecimal coherenceForDropOffAndSchoolAddress;// 不要

    private Boolean homeAddrBoolean;// 乘车地（出发地/目的地）匹配现居地中大区、小区、详细地址未同时出现
    private Boolean schoolAddrBoolean;// 乘车地（出发地/目的地）匹配公司地址（学生人群）中大区、小区、详细地址未同时出现
    private Boolean companyAddrBoolean;// 乘车地（出发地/目的地）匹配学校地址（工作人群）中大区、小区、详细地址未同时出现

    private Long diffDaysForLatestRideContainHomeAndApplyTime;// 最近一次包含居住地的打车订单距提交订单的天数
    private Long diffDaysForLatestRideContainSchoolAndApplyTime;// 最近一次包含工作地的打车订单距提交订单的天数
    private Long diffDaysForLatestRideContainCompanyAndApplyTime;// 最近一次包含学校地的打车订单距提交订单的天数

    private Long diffDaysForFirstRideContainHomeAndApplyTime;// 首次包含居住地的打车订单距提交订单的天数
    private Long diffDaysForFirstRideContainSchoolAndApplyTime;// 首次包含工作地的打车订单距提交订单的天数
    private Long diffDaysForFirstRideContainCompanyAndApplyTime;// 首次包含学校地的打车订单距提交订单的天数

    private PersonBasicInfo personBaseInfo;

    private Boolean mobilePhoneNotSame;
    private Boolean emailNotSame;

    private Long matchCompanyNum;







}

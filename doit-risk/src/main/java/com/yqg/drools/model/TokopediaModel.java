package com.yqg.drools.model;

import java.math.BigDecimal;

import io.swagger.models.auth.In;
import lombok.Getter;
import lombok.Setter;

/*****
 * @Author zengxiangcai
 * created at ${date}
 * @email zengxiangcai@yishufu.com
 *
 ****/

@Getter
@Setter
public class TokopediaModel {

    private BigDecimal recent180TotalAmount;
    private Long recent180TotalCount;
    private BigDecimal recent30TotalAmount;
    private Long recent30TotalCount;

    private BigDecimal averageCountPerMonth;
    private BigDecimal averageAmountPerMonth;

    private BigDecimal minAmountPerOrder;
    private BigDecimal maxAmountPerOrder;

    private BigDecimal averageAmountPerOrder;//订单平均交易金额
    private BigDecimal medianAmount;//订单交易价格中位数


    private String modeAmount;//交易价格众数

    private Long diffDaysBetweenFirstTransDayAndApplyDate;//借款申请时间和首笔交易时间差
    private Long diffDaysBetweenLastTransDayAndApplyDate;//借款申请时间和最后一笔交易时间差
    private Long crossDays;//首笔尾笔订单时间差
    private BigDecimal averageDiffDaysPerOrder;//平均每笔交易时间差

    private Long orderAddressCount;//订单地址个数
    private Long shopCount;//订单对应店铺数量
    private BigDecimal expenditureIncomeRatio;//支出收入比

    private Boolean emailNotSame;
    private Boolean birthdayNotSame;
    private Boolean mobileNumberNotSame;


    private Integer diffPhoneNumCount;// toko的去重手机号码个数(下单地址的手机号+注册手机号)
    private Integer matchPhoneCount;// uu手机号匹配到toko收货地址中手机号的次数
    private Integer toOrderCount;// uu手机号在toko累计下单次数
    private Long registerDiffDays;// toko账号注册距提交订单的天数
    private Integer matchNum;// toko用户名 匹配到uu用户姓名单词的个数


    private Integer bigDirectCount;//大区出现的次数
    private Integer smallDirectCount;//小区出现的次数
    private Integer detailedCount;//详细地址的单词匹配到至少1个的次数
    private String addrFlag;//大区、小区、详细地址未同时出现





}

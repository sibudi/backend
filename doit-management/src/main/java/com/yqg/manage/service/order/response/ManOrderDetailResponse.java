package com.yqg.manage.service.order.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 *  @author Jacob
 */
@Data
public class ManOrderDetailResponse {
    private Integer id;

    /**
     * 订单编号
     */
    private String uuid;

    /**
     * 下单时间
     */
    private Date createTime;

    private Date updateTime;

    private Integer createUser;

    private Integer updateUser;

    /**
     * 订单状态
     */
    private Integer status;

    /**
     * 订单步骤
     */
    private Integer orderStep;

    /**
     *进件标识
     */
    private Integer intoOrdFlag;

    /**
     * 复借订单
     */
    private Integer isRepeatBorrowing;

    /**
     * 借款次数
     */
    private Integer borrowingCount;

    /**
     * 资金渠道
     */
    private String payChannel;

    /**
     * 审核标识
     */
    private Integer checkFlag;

    /**
     *  催收标识
     */
    private Integer collectionFlag;

    /**
     * 用户角色
     */
    private Integer userRole;

    /**
     * 申请时间
     */
    private Date applyTime;

    /**
     * 申请金额
     */
    private String amountApply;

    /**
     * 申请期限
     */
    private Integer borrowingTerm;

    /**
     * 初审人员
     */
    private String firstChecker;

    /**
     * 复审人员
     */
    private String secondChecker;

    /**
     * 应还款金额
     */
    private String shouldPayAmount;

    /**
     * 实际应还款金额
     */
    private String actualPayAmount;

    /**
     * 逾期服务费
     */
    private String overdueFee;

    /**
     *逾期账户滞纳金
     */
    private String overdueMoney;

    /**
     * 借款用途
     */
    private String borrowUse;

    /**
     * 是否展期
     */
    private Integer extendType;

    /**
     * 是否结清
     */
    private Integer calType;

    /**
     *展期服务费
     */
    private String extendServiceFee;

    /**
     * 封装订单逾期多久
     */
    private String collectionLevel;

    /**
     * 标记是否实名认证通过
     */
    private Boolean realNameAuthFlag;

    /**
     * WhatsApp
     */
    private String whatsApp;

    private Boolean isKefu;
}

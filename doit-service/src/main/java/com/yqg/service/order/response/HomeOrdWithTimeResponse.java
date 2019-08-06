package com.yqg.service.order.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yqg.service.signcontract.response.SignInfoResponse;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;


/**
 * Created by wanghuaizhou on 2017/11/27.
 */
@Data
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
public class HomeOrdWithTimeResponse {

    private String orderStatus;//????
    private String orderStatusMsg;// ??????
    private String orderStep;//????
    private String orderNo;//????
    private String applicationTime;//??????
    private String borrowingAmount;//????
    private String borrowingTerm;//????
    private String arrivedAmount;//????
    private String bankCode;//???
    private String bankNumberNo;//????
    private String shouldPayTime;//?????
    private String shouldPayAmount;//?????
    private String showState;//????

    private String isDelay = "0"; // ???????????????????????? ?
    /**
     * 是否包含催收人员
     */
    private Boolean hasCollectorOrNot;
    /**
     * 催收人员phone
     */
    private String collectionPhone;
    /**
     * 催收人员WA
     */
    private String collectionWa;
    /**
     * 催收人员名称
     */
    private String collectionName;

    /**
     * 员工号码
     */
    private String employeeNumber;

    /**
     * 催收人员uuid
     */
    private String collectionUuid;


    private String rate1;
    private String rate2;
    private String rate3;
    private String rate4;
    private String rate5;
    private String rate6;


    /**
     * 签约状态信息
     */
    private SignInfoResponse signInfo;


    private String repayRate;

    private String orderType;


    // 还款计划
    private List<OrdBillResponse> billsList;

    private String currentTerm; //本期数

    private String currentNum; //本期金额

    private String currentRepayTime; //本期应还款时间

    private String currentBillNo; //本期还款账单号

    private String remainingNum;  //剩余应还款金额

    private String userScore; //用户评分

    private String couponNum; //优惠券金额

    private String isNeedQA; //是否需要填写问卷 0 未填写过 需要 1 填写过 不需要

}

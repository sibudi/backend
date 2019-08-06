package com.yqg.service.order.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * ???????1
 * Created by Didit Dwianto on 2017/11/24.
 */
@Data
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
public class HomeOrdResponse implements Serializable {

    private static final long serialVersionUID = 1255341981046416086L;
    private String orderStatus;//????
    private String orderStatusMsg;// ??????
    private String orderStep;//????
    private String orderNo;//????
    private String bankNumberNo;//????
    private String bankCode;//???
    private String arrivedAmount;//????
    private String applicationTime;//??????
    private String  borrowingAmount;//????
    private String borrowingTerm;//????
    private String showState;//????
    private String isBankCardFaild;//??????  1 ????  0 pending????

    private String rate1;
    private String rate2;
    private String rate3;
    private String rate4;
    private String rate5;
    private String rate6;

    private String repayRate;

    private String orderType;

    private String userScore; //用户评分

    private String isNeedQA; //是否需要填写问卷 0 未填写过 需要 1 填写过 不需要
}

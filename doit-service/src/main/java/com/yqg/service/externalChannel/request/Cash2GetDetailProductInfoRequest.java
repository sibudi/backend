package com.yqg.service.externalChannel.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by wanghuaizhou on 2019/1/8.
 */
@Data
public class Cash2GetDetailProductInfoRequest {

    @JsonProperty("user_idcard")
    private String userIdcard;  // 用户身份证号 当用户首次借款时，无法获取用户身份号码

    private String mobile;  // 用户手机号码

    @JsonProperty("product_id")
    private String productId;  //该值为cashcash为合作产品生成的一个ID


    @JsonProperty("application_amount")
    private BigDecimal applicationAmount;  //借款金额


    @JsonProperty("application_term")
    private Integer applicationTerm;  //借款周期


    @JsonProperty("term_unit")
    private String termUnit;  //1：天，2：月
}

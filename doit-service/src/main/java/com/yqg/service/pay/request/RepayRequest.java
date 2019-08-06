package com.yqg.service.pay.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yqg.common.models.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by wanghuaizhou on 2017/12/29.
 */
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class RepayRequest extends BaseRequest {

    @ApiModelProperty(value = "订单号")
    @JsonProperty
    private String orderNo;

    /**
     *   20180531 目前只有DOKU还款
     *   但是新增的p2p渠道还款中 BNI需要通过BNI银行还款 其他银行还是使用DOKU
     * */
    @ApiModelProperty(value = "还款类型 1 bluePay  2 xendit 3 CIMB 4 DOKU 5 BNI")
    @JsonProperty
    private String repaymentType;

    @ApiModelProperty(value = "还款渠道 1 alfamart  2 BRI 3 mandirl 4 bni 5 other banks")
    @JsonProperty
    private String repaymentChannel;

    private String type;  // 1 正常还款   2 展期还款    3 分期账单还款

    private String principal;  //  还款本金
}

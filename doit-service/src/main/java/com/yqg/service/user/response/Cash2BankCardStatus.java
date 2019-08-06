package com.yqg.service.user.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 *
 * Created by luhong on 2018/3/12.
 */
@Data
public class Cash2BankCardStatus {

    @JsonProperty(value = "order_no")
    private String orderNo; // 绑卡订单号

    @JsonProperty(value = "bind_status")
    private Integer status = 0; // 绑卡状态(0=未验证，1=待验证,2=成功,3=失败)   （0:审核中 ，1:成功 ，2:失败）

    @JsonProperty(value = "reason")
    private String reason; // 失败原因
}

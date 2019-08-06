package com.yqg.service.externalChannel.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by luhong on 2018/3/13.
 */
@Data
public class Cash2UserBankRequest {
    @JsonProperty(value = "open_bank")
    private String bankCode;//开户行简称

    @JsonProperty(value = "bank_account")
    private String bankNumberNo;//银行账号

    @JsonProperty(value = "bank_user_name")
    private String bankCardName;//账户持有人姓名11

    @JsonProperty(value = "order_no")
    private String orderNo;//订单号

    private Integer thirdType = 1;//第三方接口标识（默认是0=uang2，1=cashcash）
}

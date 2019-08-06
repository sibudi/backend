package com.yqg.service.externalChannel.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

/*****
 * @Author zengxiangcai
 * Created at 2018/3/9
 * @Email zengxiangcai@yishufu.com
 *
 ****/

@Getter
@Setter
public class Cash2LoanConfirmationParam extends Cash2BaseRequest {

    @JsonProperty(value = "order_no")
    private String orderNo;

    @JsonProperty(value = "application_amount")
    private BigDecimal applicationAmount;

    @JsonProperty(value = "application_term")
    private int applicationTerm;

    @JsonProperty(value = "term_unit")
    private int termUnit;
}

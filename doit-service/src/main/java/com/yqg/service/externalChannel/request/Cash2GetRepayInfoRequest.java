package com.yqg.service.externalChannel.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by wanghuaizhou on 2018/3/13.
 */
@Data
public class Cash2GetRepayInfoRequest {

    @JsonProperty("order_no")
    private String orderNo;

    @JsonProperty("rep_type")
    private String repType;  // 1 银行  2 便利店

    @JsonProperty("pay_type")
    private String payType;  //银行或者线上支付的方式
    //   repType 等于1时  paytype有  1001: BNI 1002: BRI  1003:MANDIRI 1004:BCA 1005: permata 1006：Other banks
    //   repType 等于2时  paytype有  2001: Alfamart 2002: Indomaret  2003:lawson 2004:2004	circle K
}

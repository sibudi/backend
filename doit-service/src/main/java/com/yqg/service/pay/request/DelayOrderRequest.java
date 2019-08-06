package com.yqg.service.pay.request;

import com.yqg.common.models.BaseRequest;
import lombok.Data;

/**
 * Created by wanghuaizhou on 2018/5/2.
 */
@Data
public class DelayOrderRequest extends BaseRequest {

    String repayNum;  // 本次还款金额

    String delayDay;  // 展期天数

    String orderNo;   // 订单号

}

package com.yqg.service.order.request;

import com.yqg.common.models.BaseRequest;
import lombok.Data;

/**
 * Created by wanghuaizhou on 2018/3/22.
 */
@Data
public class GetOrdRepayAmoutRequest extends BaseRequest {

    private String orderNo;

    private String type;  // 1 正常还款 2展期还款 3 分期订单还款
}

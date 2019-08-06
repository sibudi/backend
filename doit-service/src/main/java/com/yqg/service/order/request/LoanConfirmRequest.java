package com.yqg.service.order.request;

import com.yqg.common.models.BaseRequest;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class LoanConfirmRequest extends BaseRequest implements Serializable {
    private String orderNo;
}

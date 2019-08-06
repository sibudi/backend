package com.yqg.service.system.request;

import com.yqg.common.models.BaseRequest;
import lombok.Data;

@Data
public class LoanSwitchRequest extends BaseRequest {

    private Integer loanChannel;

    private Integer num;
}
